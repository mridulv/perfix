package io.perfix.stores.dynamodb

import com.amazonaws.auth.{AWSCredentials, AWSStaticCredentialsProvider}
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.dynamodbv2.{AmazonDynamoDB, AmazonDynamoDBClientBuilder}
import com.amazonaws.services.dynamodbv2.model._
import io.perfix.exceptions.InvalidStateException
import io.perfix.model.{ColumnDescription, DataDescription}
import io.perfix.stores.DataStore
import io.perfix.model.URLType.toDynamoDBType
import io.perfix.query.PerfixQuery

import scala.collection.JavaConverters._

class DynamoDBStore extends DataStore {
  private var client: AmazonDynamoDB = _
  private var dataDescription: DataDescription = _
  private var dynamoDBParams: DynamoDBParams = _
  private var tableParams: DynamoDBTableParams = _
  private var connectionParams: DynamoDBConnectionParams = _

  override def storeInputs(dataDescription: DataDescription): DynamoDBQuestionnaire = {
    this.dataDescription = dataDescription
    dynamoDBParams = DynamoDBParams(dataDescription)
    DynamoDBQuestionnaire(dynamoDBParams)
  }

  def connectAndInitialize(): Unit = {
    tableParams = dynamoDBParams.dynamoDBTableParams.getOrElse(throw InvalidStateException("Table Params should have been defined"))
    connectionParams = dynamoDBParams.dynamoDBConnectionParams.getOrElse(throw InvalidStateException("Connection Params should have been defined"))

    val keySchemaElements = getKeySchemaElements(dynamoDBParams.dataDescription.columns)
    val attributeDefinitions = getAttributeDefinitions(dynamoDBParams.dataDescription.columns)

    client = connectionParams.urlOpt match {
      case Some(url) => AmazonDynamoDBClientBuilder
        .standard()
        .withEndpointConfiguration(
          new AwsClientBuilder.EndpointConfiguration(url, "us-west-2")
        ).withCredentials(new AWSStaticCredentialsProvider(new AWSCredentials {
        override def getAWSAccessKeyId: String = connectionParams.accessKey
        override def getAWSSecretKey: String = connectionParams.accessSecret
      }  )).build()
      case None => AmazonDynamoDBClientBuilder
        .standard()
        .withRegion("us-west-2")
        .withCredentials(new AWSStaticCredentialsProvider(new AWSCredentials {
        override def getAWSAccessKeyId: String = connectionParams.accessKey
        override def getAWSSecretKey: String = connectionParams.accessSecret
      }  )).build()
    }

    val createTableRequest = new CreateTableRequest()
      .withTableName(tableParams.tableName)
      .withKeySchema(keySchemaElements.asJava)
      .withAttributeDefinitions(attributeDefinitions.asJava)
      .withProvisionedThroughput(new ProvisionedThroughput(5L, 5L)) // Example provisioned throughput

    client.createTable(createTableRequest)
    // This is needed for DynamoDB Table Creation Step
    Thread.sleep(10000)
  }

  override def putData(): Unit = {
    val data = dataDescription.data
    data.foreach { row =>
      val item = new java.util.HashMap[String, AttributeValue]()
      row.foreach { case (key, value) =>
        item.put(key, new AttributeValue(value.toString)) // Conversion to AttributeValue
      }

      val putItemRequest = new PutItemRequest()
        .withTableName(tableParams.tableName)
        .withItem(item)

      client.putItem(putItemRequest)
    }
  }

  override def readData(perfixQuery: PerfixQuery): Seq[Map[String, Any]] = {
    val filterExpression = translatePerfixQueryToFilterExpression(perfixQuery)

    // Assuming projectedFieldsOpt is a List of field names you want to return
    val projectionExpression = perfixQuery.projectedFieldsOpt match {
      case Some(fields) => fields.mkString(", ")
      case None => null  // or specify default fields you always want to fetch
    }

    val scanRequest = new ScanRequest()
      .withTableName(tableParams.tableName)
      .withFilterExpression(filterExpression.expression)
      .withExpressionAttributeValues(filterExpression.values)
      .withProjectionExpression(projectionExpression)

    val result = client.scan(scanRequest)
    val items = result.getItems.asScala

    items.map(item =>
      item.asScala.toMap.view.mapValues(_.toString).toMap
    ).toSeq
  }

  private def translatePerfixQueryToFilterExpression(perfixQuery: PerfixQuery): FilterExpression = {
    perfixQuery.filtersOpt match {
      case Some(filters) =>
        val expressionParts = filters.zipWithIndex.map { case (filter, index) =>
          val placeholder = s":val$index"
          filter.field + " = " + placeholder
        }
        val expression = expressionParts.mkString(" AND ")

        val attributeValues = filters.zipWithIndex.map { case (filter, index) =>
          val placeholder = s":val$index"
          placeholder -> toAttributeValue(filter.fieldValue)
        }.toMap

        FilterExpression(expression, attributeValues.asJava)

      case None =>
        FilterExpression("", Map.empty[String, AttributeValue].asJava)
    }
  }

  private def toAttributeValue(value: Any): AttributeValue = {
    value match {
      case s: String => new AttributeValue().withS(s)
      case n: Number => new AttributeValue().withN(n.toString)
      // Add more cases as needed for different data types
      case _ => throw new IllegalArgumentException("Unsupported attribute value type")
    }
  }

  private def getKeySchemaElements(columns: Seq[ColumnDescription]): Seq[KeySchemaElement] = {
    val pKey = new KeySchemaElement()
      .withAttributeName(tableParams.partitionKey)
      .withKeyType(KeyType.HASH)

    val sKey = columns.lift(1).map { col =>
      new KeySchemaElement()
        .withAttributeName(tableParams.sortKey)
        .withKeyType(KeyType.RANGE)
    }

    Seq(Some(pKey), sKey).flatten
  }

  private def getAttributeDefinitions(columns: Seq[ColumnDescription]): Seq[AttributeDefinition] = {
    columns.map { col =>
      new AttributeDefinition()
        .withAttributeName(col.columnName)
        .withAttributeType(toDynamoDBType(col.columnType))
    }
  }

  case class FilterExpression(expression: String, values: java.util.Map[String, AttributeValue])


  override def cleanup(): Unit = {
    client.deleteTable(tableParams.tableName)
    client.shutdown()
  }
}

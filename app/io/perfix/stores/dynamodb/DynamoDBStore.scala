package io.perfix.stores.dynamodb

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.dynamodbv2.{AmazonDynamoDB, AmazonDynamoDBClientBuilder}
import com.amazonaws.services.dynamodbv2.model._
import io.perfix.exceptions.InvalidStateException
import io.perfix.launch.StoreLauncher
import io.perfix.model.ColumnType.toDynamoDBType
import io.perfix.model.store.DynamoDBStoreParams
import io.perfix.model.{ColumnDescription, DatasetParams}
import io.perfix.stores.DataStore
import io.perfix.query.PerfixQuery
import io.perfix.stores.dynamodb.model.DynamoDBGSIMetadataParams

import scala.jdk.CollectionConverters._
import scala.util.control.Breaks.{break, breakable}

class DynamoDBStore(datasetParams: DatasetParams,
                    override val storeParams: DynamoDBStoreParams)
  extends DataStore[DynamoDBStoreParams] {

  private var client: AmazonDynamoDB = _
  private val dynamoDBParams: DynamoDBParams = DynamoDBParams()
  private var tableParams: DynamoDBTableParams = _

  override def launcher(): Option[StoreLauncher[DynamoDBStoreParams]] = {
    None
  }

  def connectAndInitialize(): Unit = {
    tableParams = dynamoDBParams.dynamoDBTableParams.getOrElse(throw InvalidStateException("Table Params should have been defined"))
    val capacityParams = dynamoDBParams.dynamoDBCapacityParams.getOrElse(throw InvalidStateException("Capacity Params should have been defined"))

    val keySchemaElements = getKeySchemaElements(
      tableParams.partitionKey,
      tableParams.sortKey
    )
    val attributeDefinitions = getAttributeDefinitions(datasetParams.columns)

    val credentialsProvider = DefaultAWSCredentialsProviderChain.getInstance()

    client = tableParams.urlOpt match {
      case Some(url) => AmazonDynamoDBClientBuilder
        .standard()
        .withEndpointConfiguration(
          new AwsClientBuilder.EndpointConfiguration(url, "us-west-2")
        ).withCredentials(credentialsProvider).build()
      case None => AmazonDynamoDBClientBuilder
        .standard()
        .withRegion("us-west-2")
        .withCredentials(credentialsProvider).build()
    }

    val readCapacity: Long = capacityParams.readCapacity.getOrElse(5L)
    val writeCapacity: Long = capacityParams.writeCapacity.getOrElse(5L)
    val provisionedThroughput = new ProvisionedThroughput(readCapacity, writeCapacity)

    val createTableRequest = new CreateTableRequest()
      .withTableName(tableParams.tableName)
      .withKeySchema(keySchemaElements.asJava)
      .withAttributeDefinitions(attributeDefinitions.asJava)
      .withProvisionedThroughput(provisionedThroughput)

    client.createTable(createTableRequest)
    waitForMainTable()

    dynamoDBParams.dynamoDBGSIMetadataParams match {
      case Some(gsi) =>
        val updateTableRequest = new UpdateTableRequest()
          .withTableName(tableParams.tableName)
          .withAttributeDefinitions(attributeDefinitions: _*)
          .withGlobalSecondaryIndexUpdates(createGSIs(gsi): _*)
        client.updateTable(updateTableRequest)
        waitForGSI()
      case None =>
    }

    Thread.sleep(5000)
  }

  override def putData(rows: Seq[Map[String, Any]]): Unit = {
    val requests = rows.map { row =>
      val item = new java.util.HashMap[String, AttributeValue]()
      row.foreach { case (key, value) =>
        item.put(key, new AttributeValue(value.toString)) // Conversion to AttributeValue
      }

      val putItemRequest = new PutRequest()
        .withItem(item)
      val writeRequest = new WriteRequest()
      writeRequest.withPutRequest(putItemRequest)
      writeRequest
    }
    val batchWriteItemRequest = new BatchWriteItemRequest().withRequestItems(Map(tableParams.tableName -> requests.asJava).asJava)
    client.batchWriteItem(batchWriteItemRequest)
  }

  override def readData(perfixQuery: PerfixQuery): Seq[Map[String, Any]] = {
    val filterExpression = translatePerfixQueryToFilterExpression(perfixQuery)

    // Assuming projectedFieldsOpt is a List of field names you want to return
    val projectionExpression = perfixQuery.projectedFieldsOpt match {
      case Some(fields) => fields.mkString(", ")
      case None => null  // or specify default fields you always want to fetch
    }

    val result = findRelevantTableOpt(perfixQuery) match {
      case Some(table) =>
        val queryRequest = new QueryRequest()
          .withTableName(table)
          .withKeyConditionExpression(filterExpression.expression)
          .withExpressionAttributeValues(filterExpression.values)
          .withProjectionExpression(projectionExpression)
        val res = client.query(queryRequest)
        res.getItems
      case None =>
        val scanRequest = new ScanRequest()
          .withTableName(tableParams.tableName)
          .withFilterExpression(filterExpression.expression)
          .withExpressionAttributeValues(filterExpression.values)
          .withProjectionExpression(projectionExpression)
        val res = client.scan(scanRequest)
        res.getItems
    }

    val items = result.asScala

    items.map(item =>
      item.asScala.toMap.view.mapValues(_.toString).toMap
    ).toSeq
  }

  private def findRelevantTableOpt(perfixQuery: PerfixQuery): Option[String] = {
    dynamoDBParams.indexes().find(i => i.validForFilters(perfixQuery)).map(_.tableName)
  }

  private def createGSIs(dynamoDBGSIMetadataParams: DynamoDBGSIMetadataParams): Seq[GlobalSecondaryIndexUpdate] = {
    dynamoDBGSIMetadataParams.gsiParams.map { gsiParam =>

      val keySchema = getKeySchemaElements(gsiParam.partitionKey, gsiParam.sortKey)
      val provisionedThroughput = new ProvisionedThroughput(5L, 5L)
      val projection = new Projection().withProjectionType(ProjectionType.ALL)
      val globalSecondaryIndexAction = new CreateGlobalSecondaryIndexAction()
        .withIndexName(gsiParam.tableName)
        .withKeySchema(keySchema: _*)
        .withProjection(projection)
        .withProvisionedThroughput(provisionedThroughput)

      new GlobalSecondaryIndexUpdate().withCreate(globalSecondaryIndexAction)
    }
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

  private def getKeySchemaElements(partitionKey: String,
                                   sortKey: String): Seq[KeySchemaElement] = {
    val pKey = new KeySchemaElement()
      .withAttributeName(partitionKey)
      .withKeyType(KeyType.HASH)

    val sKey = new KeySchemaElement()
      .withAttributeName(sortKey)
      .withKeyType(KeyType.RANGE)

    Seq(Some(pKey), Some(sKey)).flatten
  }

  private def getAttributeDefinitions(columns: Seq[ColumnDescription]): Seq[AttributeDefinition] = {
    columns.map { col =>
      new AttributeDefinition()
        .withAttributeName(col.columnName)
        .withAttributeType(toDynamoDBType(col.columnType))
    }
  }

  private def waitForMainTable(): Unit = {
    breakable {
      while (true) {
        val describeTableRequest = new DescribeTableRequest().withTableName(tableParams.tableName)
        val tableDescription = client.describeTable(describeTableRequest).getTable
        if (tableDescription.getTableStatus.toLowerCase() == "active") {
          break
        }
        println("Waiting for the table to become active")
        Thread.sleep(1000)
      }
    }
  }

  private def waitForGSI(): Unit = {
    breakable {
      while (true) {
        val describeTableRequest = new DescribeTableRequest().withTableName(tableParams.tableName)
        val tableDescription = client.describeTable(describeTableRequest).getTable
        val globalSecondaryIndexes = tableDescription.getGlobalSecondaryIndexes
        val checkForAllGSI = globalSecondaryIndexes.asScala.forall(_.getIndexStatus.toLowerCase() == "active")
        if (globalSecondaryIndexes.isEmpty || checkForAllGSI) {
          break
        }
        println("Waiting for the GSI to become active")
        Thread.sleep(1000)
      }
    }
  }

  case class FilterExpression(expression: String, values: java.util.Map[String, AttributeValue])

  override def cleanup(): Unit = {
    client.deleteTable(tableParams.tableName)
    client.shutdown()
  }
}

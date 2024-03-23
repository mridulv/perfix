package io.perfix.stores.dynamodb

import io.perfix.question.QuestionParams
import io.perfix.stores.dynamodb.model.{DynamoDBGSIMetadataParams, DynamoDBIndex}

case class DynamoDBParams() extends QuestionParams {
  var dynamoDBTableParams: Option[DynamoDBTableParams] = None
  var dynamoDBCapacityParams: Option[DynamoDBCapacityParams] = None
  var dynamoDBGSIMetadataParams: Option[DynamoDBGSIMetadataParams] = None

  def isEmpty: Boolean = {
    dynamoDBTableParams.isEmpty && dynamoDBCapacityParams.isEmpty
  }

  def indexes(): Seq[DynamoDBIndex] = {
    val primaryIndex = dynamoDBTableParams.map(p => DynamoDBIndex(p.tableName, p.partitionKey, p.sortKey))
    val secondaryIndexes = dynamoDBGSIMetadataParams
      .map(_.gsiParams)
      .getOrElse(Seq.empty)
      .map(p => DynamoDBIndex(p.tableName, p.partitionKey, p.sortKey))

    Seq(primaryIndex).flatten ++ secondaryIndexes
  }
}

case class DynamoDBTableParams(urlOpt: Option[String], tableName: String, partitionKey: String, sortKey: String)
case class DynamoDBCapacityParams(readCapacity: Option[Long], writeCapacity: Option[Long])

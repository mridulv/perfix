package io.perfix.stores.dynamodb

import io.perfix.model.DataDescription
import io.perfix.question.QuestionParams
import io.perfix.stores.dynamodb.model.DynamoDBGSIMetadataParams

case class DynamoDBParams(dataDescription: DataDescription) extends QuestionParams {
  var dynamoDBConnectionParams: Option[DynamoDBConnectionParams] = None
  var dynamoDBTableParams: Option[DynamoDBTableParams] = None
  var dynamoDBCapacityParams: Option[DynamoDBCapacityParams] = None
  var dynamoDBGSIMetadataParams: Option[DynamoDBGSIMetadataParams] = None

  def isEmpty: Boolean = {
    dynamoDBConnectionParams.isEmpty && dynamoDBTableParams.isEmpty && dynamoDBCapacityParams.isEmpty
  }
}

case class DynamoDBConnectionParams(urlOpt: Option[String], accessKey: String, accessSecret: String)
case class DynamoDBTableParams(tableName: String, partitionKey: String, sortKey: String)
case class DynamoDBCapacityParams(readCapacity: Option[Long], writeCapacity: Option[Long])
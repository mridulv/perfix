package io.perfix.stores.dynamodb

import io.perfix.model.DataDescription
import io.perfix.question.QuestionParams

case class DynamoDBParams(dataDescription: DataDescription) extends QuestionParams {
  var dynamoDBConnectionParams: Option[DynamoDBConnectionParams] = None
  var dynamoDBTableParams: Option[DynamoDBTableParams] = None

  def isEmpty: Boolean = {
    dynamoDBConnectionParams.isEmpty && dynamoDBTableParams.isEmpty
  }
}

case class DynamoDBConnectionParams(url: String, accessKey: String, accessSecret: String)
case class DynamoDBTableParams(tableName: String, partitionKey: String, sortKey: String)
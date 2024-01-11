package io.perfix.stores.dynamodb

import io.perfix.model.DataDescription
import io.perfix.question.QuestionParams

case class DynamoDBParams(dataDescription: DataDescription) extends QuestionParams {
  var dynamoDBConnectionParams: Option[DynamoDBConnectionParams] = None
  var dynamoDBTableParams: Option[DynamoDBTableParams] = None
  var dynamoDBCapacityParams: Option[DynamoDBCapacityParams] = None

  def isEmpty: Boolean = {
    dynamoDBConnectionParams.isEmpty && dynamoDBTableParams.isEmpty && dynamoDBCapacityParams.isEmpty
  }
}

case class DynamoDBConnectionParams(urlOpt: Option[String], accessKey: String, accessSecret: String)
case class DynamoDBTableParams(tableName: String, partitionKey: String, sortKey: String)
case class DynamoDBCapacityParams(readCapacity: Option[Int], writeCapacity: Option[Int])
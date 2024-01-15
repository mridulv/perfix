package io.perfix.stores.dynamodb.model

import play.api.libs.json.{Format, Json}

case class DynamoDBGSIParam(partitionKey: String, sortKey: String)

object DynamoDBGSIParam {
  implicit val DynamoDBGSIParamFormatter: Format[DynamoDBGSIParam] = Json.format[DynamoDBGSIParam]
}


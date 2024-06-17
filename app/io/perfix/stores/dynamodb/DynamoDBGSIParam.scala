package io.perfix.stores.dynamodb

import play.api.libs.json.{Format, Json}

case class DynamoDBGSIParam(partitionKey: String, sortKey: String) {

  def tableName: String = {
    s"gsi_${partitionKey}_${sortKey}"
  }

}

object DynamoDBGSIParam {
  implicit val DynamoDBGSIParamFormatter: Format[DynamoDBGSIParam] = Json.format[DynamoDBGSIParam]
}


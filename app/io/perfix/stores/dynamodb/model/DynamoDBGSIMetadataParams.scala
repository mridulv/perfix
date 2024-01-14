package io.perfix.stores.dynamodb.model

import play.api.libs.json.{Format, Json}

case class DynamoDBGSIMetadataParams(gsiParams: Seq[DynamoDBGSIParam])

case class DynamoDBGSIParam(partitionKey: String, sortKey: String)

object DynamoDBGSIMetadataParams {
  implicit val DynamoDBGSIMetadataParamsFormatter: Format[DynamoDBGSIMetadataParams] = Json.format[DynamoDBGSIMetadataParams]
  implicit val DynamoDBGSIParamFormatter: Format[DynamoDBGSIParam] = Json.format[DynamoDBGSIParam]
}

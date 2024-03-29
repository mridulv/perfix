package io.perfix.stores.dynamodb.model

import play.api.libs.json.{Format, Json}

case class DynamoDBGSIMetadataParams(gsiParams: Seq[DynamoDBGSIParam])

object DynamoDBGSIMetadataParams {
  implicit val DynamoDBGSIMetadataParamsFormatter: Format[DynamoDBGSIMetadataParams] = Json.format[DynamoDBGSIMetadataParams]
}

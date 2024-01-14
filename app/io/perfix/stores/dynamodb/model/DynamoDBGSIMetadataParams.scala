package io.perfix.stores.dynamodb.model

import play.api.libs.json.Format

case class DynamoDBGSIMetadataParams(gsiParams: Seq[DynamoDBGSIParam])

case class DynamoDBGSIParam(partitionKey: String, sortKey: String)

object DynamoDBGSIMetadataParams {
  implicit val DynamoDBGSIMetadataParamsFormatter: Format[DynamoDBGSIMetadataParams] = Format[DynamoDBGSIMetadataParams]
  implicit val DynamoDBGSIParamFormatter: Format[DynamoDBGSIParam] = Format[DynamoDBGSIParam]
}

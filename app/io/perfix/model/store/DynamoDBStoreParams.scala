package io.perfix.model.store

import io.perfix.stores.dynamodb.model.DynamoDBGSIParam
import play.api.libs.json.{Format, Json}

case class DynamoDBStoreParams(rcu: Int,
                               wcu: Int,
                               gsiParams: Seq[DynamoDBGSIParam]) extends StoreParams

object DynamoDBStoreParams {
  implicit val DynamoDBStoreParamsFormatter: Format[DynamoDBStoreParams] = Json.format[DynamoDBStoreParams]
}


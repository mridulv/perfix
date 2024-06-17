package io.perfix.stores.dynamodb

import io.perfix.model.store.StoreParams
import play.api.libs.json.{Format, Json}

case class DynamoDBStoreParams(tableName: String,
                               rcu: Long,
                               wcu: Long,
                               partitionKey: String,
                               sortKey: String,
                               gsiParams: Seq[DynamoDBGSIParam]) extends StoreParams

object DynamoDBStoreParams {
  implicit val DynamoDBStoreParamsFormatter: Format[DynamoDBStoreParams] = Json.format[DynamoDBStoreParams]
}


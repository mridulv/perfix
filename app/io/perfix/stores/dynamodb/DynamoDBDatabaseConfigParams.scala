package io.perfix.stores.dynamodb

import io.perfix.model.store.DatabaseConfigParams
import play.api.libs.json.{Format, Json}

case class DynamoDBDatabaseConfigParams(tableName: String,
                                        rcu: Long,
                                        wcu: Long,
                                        partitionKey: String,
                                        sortKey: String,
                                        gsiParams: Seq[DynamoDBGSIParam]) extends DatabaseConfigParams

object DynamoDBDatabaseConfigParams {
  implicit val DynamoDBStoreParamsFormatter: Format[DynamoDBDatabaseConfigParams] = Json.format[DynamoDBDatabaseConfigParams]
}


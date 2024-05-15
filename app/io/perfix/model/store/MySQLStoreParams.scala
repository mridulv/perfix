package io.perfix.model.store

import play.api.libs.json.{Format, Json}

case class MySQLStoreParams(instanceType: String = "db.t3.medium",
                            tableName: String,
                            primaryIndexColumn: Option[String],
                            secondaryIndexesColumn: Option[Seq[String]]) extends StoreParams

object MySQLStoreParams {
  implicit val MySQLStoreParamsFormatter: Format[MySQLStoreParams] = Json.format[MySQLStoreParams]
}

package io.perfix.stores.mysql

import io.perfix.model.store.DatabaseConfigParams
import play.api.libs.json.{Format, Json}

case class MySQLDatabaseConfigParams(instanceType: String = "db.t3.medium",
                                     tableName: String,
                                     primaryIndexColumn: Option[String],
                                     secondaryIndexesColumn: Option[Seq[String]]) extends DatabaseConfigParams

object MySQLDatabaseConfigParams {
  implicit val MySQLStoreParamsFormatter: Format[MySQLDatabaseConfigParams] = Json.format[MySQLDatabaseConfigParams]
}

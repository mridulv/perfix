package io.perfix.stores.mysql

import io.perfix.model.store.DatabaseSetupParams
import play.api.libs.json.{Format, Json}

case class MySQLDatabaseSetupParams(instanceType: String = "db.t3.medium",
                                    tableName: String,
                                    primaryIndexColumn: Option[String],
                                    secondaryIndexesColumn: Option[Seq[String]],
                                    dbName: Option[String] = None,
                                    dbDetails: Option[MySQLConnectionParams] = None) extends DatabaseSetupParams

case class MySQLConnectionParams(url: String, username: String, password: String)

object MySQLDatabaseSetupParams {
  implicit val MySQLStoreParamsFormatter: Format[MySQLDatabaseSetupParams] = Json.format[MySQLDatabaseSetupParams]

  implicit val MySQLConnectionParamsFormatter: Format[MySQLConnectionParams] = Json.format[MySQLConnectionParams]
}

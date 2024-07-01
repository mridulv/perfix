package io.perfix.stores.mysql

import io.perfix.model.store.DatabaseSetupParams
import io.perfix.model.store.StoreType.{MySQL, StoreType}
import play.api.libs.json.{Format, Json}
import RDSDatabaseSetupParams._

case class RDSDatabaseSetupParams(instanceType: String = "db.t3.medium",
                                  tableName: String,
                                  primaryIndexColumn: Option[String],
                                  secondaryIndexesColumn: Option[Seq[String]],
                                  dbName: Option[String] = None,
                                  dbDetails: Option[MySQLConnectionParams] = None,
                                  databaseType: Option[StoreType] = Some(MySQL)) extends DatabaseSetupParams

object RDSDatabaseSetupParams {
  implicit val RDSDatabaseSetupParamsFormatter: Format[RDSDatabaseSetupParams] = Json.format[RDSDatabaseSetupParams]
}

case class MySQLConnectionParams(url: String, username: String, password: String)

object MySQLConnectionParams {
  implicit val MySQLConnectionParamsFormatter: Format[MySQLConnectionParams] = Json.format[MySQLConnectionParams]
}

package io.perfix.stores.mysql

import io.perfix.model.store.DatabaseConfigParams
import play.api.libs.json.{Format, Json}

case class MySQLDatabaseConfigParams(instanceType: String = "db.t3.medium",
                                     tableName: String,
                                     primaryIndexColumn: Option[String],
                                     secondaryIndexesColumn: Option[Seq[String]]) extends DatabaseConfigParams {

  var mySQLConnectionParams: Option[MySQLConnectionParams] = None
  var mySQLTableParams: Option[MySQLTableParams] = None
  var mySQLTableIndexesParams: Option[MySQLTableIndexesParams] = None

}

case class MySQLConnectionParams(url: String, username: String, password: String)
case class MySQLTableParams(dbName: String, tableName: String)
case class MySQLTableIndexesParams(primaryIndexColumn: Option[String], secondaryIndexesColumn: Option[Seq[String]])

object MySQLDatabaseConfigParams {
  implicit val MySQLStoreParamsFormatter: Format[MySQLDatabaseConfigParams] = Json.format[MySQLDatabaseConfigParams]
}

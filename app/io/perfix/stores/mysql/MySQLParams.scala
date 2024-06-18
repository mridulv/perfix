package io.perfix.stores.mysql

case class MySQLParams() {

  var mySQLConnectionParams: Option[MySQLConnectionParams] = None
  var mySQLTableParams: Option[MySQLTableParams] = None
  var mySQLTableIndexesParams: Option[MySQLTableIndexesParams] = None

}

case class MySQLConnectionParams(url: String, username: String, password: String)
case class MySQLTableParams(dbName: String, tableName: String)
case class MySQLTableIndexesParams(primaryIndexColumn: Option[String], secondaryIndexesColumn: Option[Seq[String]])
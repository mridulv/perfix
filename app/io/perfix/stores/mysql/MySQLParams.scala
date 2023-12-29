package io.perfix.stores.mysql

import io.perfix.model.DataDescription
import io.perfix.question.QuestionParams

case class MySQLParams(dataDescription: DataDescription) extends QuestionParams {

  var mySQLConnectionParams: Option[MySQLConnectionParams] = None
  var mySQLTableParams: Option[MySQLTableParams] = None

}

case class MySQLConnectionParams(url: String, username: String, password: String)
case class MySQLTableParams(dbName: String, tableName: String)
package io.perfix.query

import io.perfix.query.DBQuery.{DBQueryReads, DBQueryWrites}
import play.api.libs.json.{Format, Json}

case class SqlDBQuery(sql: String) extends DBQuery {

  def convert(tableNames: Seq[String], dbName: String): String = {
    tableNames.foldLeft(sql) { case (sql, tableName) =>
      sql.replace(tableName, s"$dbName.$tableName")
    }
  }

}

object SqlDBQuery {
  implicit val SqlDBQueryFormatter: Format[SqlDBQuery] = Json.format[SqlDBQuery]
}

package io.perfix.query

import io.perfix.query.DBQuery.{DBQueryReads, DBQueryWrites}
import play.api.libs.json.{Format, Json}

case class SqlDBQuery(sql: String) extends DBQuery {

  def convert(tableNames: Seq[String], dbName: String): String = {
    tableNames.foldLeft(sql) { case (sql, tableName) =>
      sql.replace(tableName, s"$dbName.$tableName")
    }
  }

  def resolve(mapping: Map[String, Any]): SqlDBQuery = {
    val resolvedSql = mapping.foldLeft(sql) {
      case (resolvedSql, (placeholder, value)) =>
        resolvedSql.replace(s"{{$placeholder}}", value.toString)
    }
    SqlDBQuery(resolvedSql)
  }
}

object SqlDBQuery {
  implicit val SqlDBQueryFormatter: Format[SqlDBQuery] = Json.format[SqlDBQuery]
}

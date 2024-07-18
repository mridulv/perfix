package io.perfix.query

import play.api.libs.json.{Format, Json}

case class NoSqlDBQuery(filters: List[DbQueryFilter]) extends DBQuery

object NoSqlDBQuery {
  implicit val NoSqlDBQueryFormatter: Format[NoSqlDBQuery] = Json.format[NoSqlDBQuery]
}

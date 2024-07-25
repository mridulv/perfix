package io.perfix.query

import play.api.libs.json.{Format, Json}

case class NoSqlDBQuery(filters: List[DbQueryFilter]) extends DBQuery {
  def resolve(mapping: Map[String, Any]): DBQuery = {
    NoSqlDBQuery(filters.map(_.resolve(mapping)))
  }

}

object NoSqlDBQuery {
  implicit val NoSqlDBQueryFormatter: Format[NoSqlDBQuery] = Json.format[NoSqlDBQuery]
}

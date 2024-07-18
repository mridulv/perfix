package io.perfix.query

import play.api.libs.json._

trait DBQuery

object DBQuery {
  val Sql = "sql"
  val SqlBuilder = "sql-builder"
  val NoSql = "nosql"

  implicit val DBQueryReads: Reads[DBQuery] = (json: JsValue) => {
    (json \ "type").validate[String] flatMap {
      case Sql            => json.validate[SqlDBQuery]
      case SqlBuilder     => json.validate[SqlDBQueryBuilder]
      case NoSql          => json.validate[NoSqlDBQuery]
      case other          => JsError(s"Unknown type: $other")
    }
  }

  implicit val DBQueryWrites: Writes[DBQuery] = {
    case query: SqlDBQuery            => Json.toJson(query).as[JsObject] + ("type" -> JsString(Sql))
    case query: SqlDBQueryBuilder     => Json.toJson(query).as[JsObject] + ("type" -> JsString(SqlBuilder))
    case query: NoSqlDBQuery          => Json.toJson(query).as[JsObject] + ("type" -> JsString(NoSql))
  }

  implicit val DBQueryFormatter: Format[DBQuery] = Format(DBQueryReads, DBQueryWrites)
}

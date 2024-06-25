package io.perfix.model

import io.perfix.model.DatabaseCategory.DatabaseCategory
import play.api.libs.json.{Format, JsError, JsString, JsSuccess, Reads, Writes}

object DatabaseCategory extends Enumeration {
  type DatabaseCategory = Value

  val AWS_RDBMS, AWS_NOSQL, RDBMS, NoSQL, VectorDB, GraphDB = Value

  implicit val writes: Writes[DatabaseCategory] = Writes[DatabaseCategory] {
    case AWS_RDBMS => JsString("AWS Cloud - RDBMS Databases")
    case AWS_NOSQL => JsString("AWS Cloud - NoSQL Databases")
    case RDBMS => JsString("RDBMS Databases")
    case NoSQL => JsString("NoSQL Databases")
    case VectorDB => JsString("Vector Databases")
    case GraphDB => JsString("Graph Databases")
  }

  implicit val reads: Reads[DatabaseCategory] = Reads[DatabaseCategory] {
    case JsString(str) => JsSuccess(DatabaseCategory.withName(str))
    case _ => JsError("Invalid value for DatabaseCategory")
  }

  implicit val StoreTypeFormat: Format[DatabaseCategory] = Format(reads, writes)

}


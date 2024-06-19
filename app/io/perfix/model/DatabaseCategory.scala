package io.perfix.model

import play.api.libs.json.{Format, JsError, JsString, JsSuccess, Reads, Writes}

object DatabaseCategory extends Enumeration {
  type DatabaseCategory = Value

  val AWS_RDBMS, AWS_NOSQL, RDBMS, NoSQL, VectorDB, GraphDB = Value

  implicit val writes: Writes[DatabaseCategory] = Writes[DatabaseCategory] { category =>
    JsString(category.toString)
  }

  implicit val reads: Reads[DatabaseCategory] = Reads[DatabaseCategory] {
    case JsString(str) => JsSuccess(DatabaseCategory.withName(str))
    case _ => JsError("Invalid value for DatabaseCategory")
  }

  implicit val StoreTypeFormat: Format[DatabaseCategory] = Format(reads, writes)

}


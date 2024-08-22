package io.perfix.model

import play.api.libs.json.{Format, JsError, JsString, JsSuccess, Reads, Writes}

trait DatabaseCategory

case object AWS_RDBMS extends DatabaseCategory {
  override def toString: String = "AWS Cloud - RDBMS Databases"
}

case object AWS_NOSQL extends DatabaseCategory {
  override def toString: String = "AWS Cloud - NoSQL Databases"
}

case object RDBMS extends DatabaseCategory {
  override def toString: String = "RDBMS Databases"
}

case object NoSQL extends DatabaseCategory {
  override def toString: String = "NoSQL Databases"
}

case object VectorDB extends DatabaseCategory {
  override def toString: String = "Vector Databases"
}

case object GraphDB extends DatabaseCategory {
  override def toString: String = "Graph Databases"
}

object DatabaseCategory {

  implicit val writes: Writes[DatabaseCategory] = Writes[DatabaseCategory] { databaseCategory =>
    JsString(databaseCategory.toString)
  }

  implicit val reads: Reads[DatabaseCategory] = Reads[DatabaseCategory] {
    case JsString(str) =>
      AllDatabaseCategories
        .find(_.toString == str)
        .map(category => JsSuccess(category))
        .getOrElse(JsError("Invalid value for DatabaseCategory"))
    case _ => JsError("Invalid value for DatabaseCategory")
  }

  implicit val StoreTypeFormat: Format[DatabaseCategory] = Format(reads, writes)

  val AllDatabaseCategories = Seq(AWS_RDBMS, AWS_NOSQL, RDBMS, NoSQL, VectorDB, GraphDB)

}


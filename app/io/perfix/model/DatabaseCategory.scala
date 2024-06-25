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
    case JsString(str) => str match {
      case "AWS_RDBMS" => JsSuccess(AWS_RDBMS)
      case "AWS_NOSQL" => JsSuccess(AWS_NOSQL)
      case "RDBMS" => JsSuccess(RDBMS)
      case "NoSQL" => JsSuccess(NoSQL)
      case "VectorDB" => JsSuccess(VectorDB)
      case "GraphDB" => JsSuccess(GraphDB)
      case _ => JsError("Invalid value for DatabaseCategory")
    }
    case _ => JsError("Invalid value for DatabaseCategory")
  }

  implicit val StoreTypeFormat: Format[DatabaseCategory] = Format(reads, writes)

}


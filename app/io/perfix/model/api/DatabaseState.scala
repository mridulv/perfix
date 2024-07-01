package io.perfix.model.api

import play.api.libs.json.{Format, JsError, JsString, JsSuccess, Reads, Writes}

object DatabaseState extends Enumeration {

  type DatabaseState = Value
  val NotStarted, Created, InProgress, Failed = Value

  implicit val writes: Writes[DatabaseState] = Writes[DatabaseState] { databaseState =>
    JsString(databaseState.toString)
  }

  implicit val reads: Reads[DatabaseState] = Reads[DatabaseState] {
    case JsString(str) => JsSuccess(DatabaseState.withName(str))
    case _ => JsError("Invalid value for DatabaseState")
  }

  implicit val DatabaseStateFormat: Format[DatabaseState] = Format(reads, writes)

}

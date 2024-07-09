package io.perfix.model.api

import play.api.libs.json.{Format, JsError, JsString, JsSuccess, Reads, Writes}

object UseCaseState extends Enumeration {

  type UseCaseState = Value
  val Created, InProgress, Completed, Failed = Value

  implicit val writes: Writes[UseCaseState] = Writes[UseCaseState] { useCaseState =>
    JsString(useCaseState.toString)
  }

  implicit val reads: Reads[UseCaseState] = Reads[UseCaseState] {
    case JsString(str) => JsSuccess(UseCaseState.withName(str))
    case _ => JsError("Invalid value for Conversation State")
  }

  implicit val DBExplorerStateFormat: Format[UseCaseState] = Format(reads, writes)
}

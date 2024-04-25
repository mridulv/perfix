package io.perfix.model

import play.api.libs.json.{Format, JsError, JsString, JsSuccess, Reads, Writes}

trait FormStatus
case object NotStarted extends FormStatus
case object InComplete extends FormStatus
case object Completed extends FormStatus

object FormStatus {
  // Define a custom Reads for deserialization
  implicit val FormStatusReads: Reads[FormStatus] = Reads {
    case JsString("NotStarted") => JsSuccess(NotStarted)
    case JsString("InComplete") => JsSuccess(InComplete)
    case JsString("Completed") => JsSuccess(Completed)
    case _ => JsError("Invalid DataType")
  }

  // Define a custom Writes for serialization
  implicit val FormStatusWrites: Writes[FormStatus] = Writes {
    case NotStarted => JsString("NotStarted")
    case InComplete => JsString("InComplete")
    case Completed => JsString("Completed")
  }

  // Define a Format that combines Reads and Writes
  implicit val FormStatusFormat: Format[FormStatus] = Format(FormStatusReads, FormStatusWrites)
}


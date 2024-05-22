package io.perfix.model

import play.api.libs.json._

trait FormStatus

case object InComplete extends FormStatus {
  override def toString: String = "InComplete"
}

case object Completed extends FormStatus {
  override def toString: String = "Completed"
}

case object Updating extends FormStatus {
  override def toString: String = "Updating"
}

object FormStatus {
  // Define a custom Reads for deserialization
  implicit val FormStatusReads: Reads[FormStatus] = Reads {
    case JsString("InComplete") => JsSuccess(InComplete)
    case JsString("Completed") => JsSuccess(Completed)
    case JsString("Updating") => JsSuccess(Updating)
    case _ => JsError("Invalid DataType")
  }

  // Define a custom Writes for serialization
  implicit val FormStatusWrites: Writes[FormStatus] = Writes {
    case InComplete => JsString("InComplete")
    case Completed => JsString("Completed")
    case Updating => JsString("Updating")
  }

  // Define a Format that combines Reads and Writes
  implicit val FormStatusFormat: Format[FormStatus] = Format(FormStatusReads, FormStatusWrites)
}


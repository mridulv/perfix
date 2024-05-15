package io.perfix.model.experiment

import play.api.libs.json.{Format, JsError, JsString, JsSuccess, Reads, Writes}

sealed trait ExperimentState {
  def name: String
}

object ExperimentState {
  case object Created extends ExperimentState {
    val name = "Created"
  }

  case object InProgress extends ExperimentState {
    val name = "InProgress"
  }

  case object Completed extends ExperimentState {
    val name = "Completed"
  }

  case object Failed extends ExperimentState {
    val name = "Failed"
  }

  implicit val ExperimentStateReads: Reads[ExperimentState] = Reads {
    case JsString("Created") => JsSuccess(Created)
    case JsString("InProgress") => JsSuccess(InProgress)
    case JsString("Completed") => JsSuccess(Completed)
    case JsString("Failed") => JsSuccess(Failed)
    case _ => JsError("Invalid DataType")
  }

  // Define a custom Writes for serialization
  implicit val ExperimentStateWrites: Writes[ExperimentState] = Writes {
    case Created => JsString("Created")
    case InProgress => JsString("InProgress")
    case Completed => JsString("Completed")
    case Failed => JsString("Failed")
  }

  implicit val ExperimentStateWritesFormat: Format[ExperimentState] = Format(ExperimentStateReads, ExperimentStateWrites)
}

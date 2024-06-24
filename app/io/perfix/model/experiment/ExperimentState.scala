package io.perfix.model.experiment

import play.api.libs.json._

object ExperimentState extends Enumeration {

  type ExperimentState = Value
  val Created, InProgress, Completed, Failed = Value

  implicit val writes: Writes[ExperimentState] = Writes[ExperimentState] { experimentState =>
    JsString(experimentState.toString)
  }

  implicit val reads: Reads[ExperimentState] = Reads[ExperimentState] {
    case JsString(str) => JsSuccess(ExperimentState.withName(str))
    case _ => JsError("Invalid value for Experiment State")
  }

  implicit val ExperimentStateFormat: Format[ExperimentState] = Format(reads, writes)
}

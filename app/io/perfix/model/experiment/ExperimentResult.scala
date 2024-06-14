package io.perfix.model.experiment

import play.api.libs.json.{Format, JsError, JsObject, JsString, JsValue, Json, Reads, Writes}

trait ExperimentResult

object ExperimentResult {
  implicit val experimentResultWrites: Writes[ExperimentResult] = {
    case single: SingleExperimentResult => Json.toJson(single)(SingleExperimentResult.SingleExperimentResultFormatter).as[JsObject] + ("type" -> JsString("single"))
    case multiple: MultipleExperimentResult => Json.toJson(multiple)(MultipleExperimentResult.MultipleExperimentResultFormatter).as[JsObject] + ("type" -> JsString("multiple"))
  }

  implicit val experimentResultReads: Reads[ExperimentResult] = (json: JsValue) => {
    (json \ "type").validate[String].flatMap {
      case "single" => json.validate[SingleExperimentResult]
      case "multiple" => json.validate[MultipleExperimentResult]
      case other => JsError(s"Unknown type: $other")
    }
  }

  implicit val ExperimentResultFormatter: Format[ExperimentResult] = Format(experimentResultReads, experimentResultWrites)
}

case class PercentileLatency(percentile: Int, latency: Double)

object PercentileLatency {
  implicit val PercentileLatencyFormatter: Format[PercentileLatency] = Json.format[PercentileLatency]
}

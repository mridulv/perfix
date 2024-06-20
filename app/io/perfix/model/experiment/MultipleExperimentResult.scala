package io.perfix.model.experiment

import play.api.libs.json.{Format, Json}

case class MultipleExperimentResult(results: Seq[SingleExperimentResult])

object MultipleExperimentResult {
  implicit val MultipleExperimentResultFormatter: Format[MultipleExperimentResult] = Json.format[MultipleExperimentResult]
}

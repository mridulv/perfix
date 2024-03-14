package io.perfix.model

import play.api.libs.json.{Format, Json, Writes}

case class ExperimentRunParams(rows: Int,
                               batchSize: Int,
                               benchmarkTimeSeconds: Int)

object ExperimentRunParams {
  implicit val ExperimentRunParams: Format[ExperimentRunParams] = Json.format[ExperimentRunParams]
}

package io.perfix.model.experiment

import play.api.libs.json.{Format, Json}

case class SingleExperimentResult(overallQueryTime: Long,
                                  overallWriteTimeTaken: Long,
                                  numberOfCalls: Int,
                                  queryLatencies: Seq[PercentileLatency],
                                  writeLatencies: Seq[PercentileLatency]) extends ExperimentResult

object SingleExperimentResult {
  implicit val SingleExperimentResultFormatter: Format[SingleExperimentResult] = Json.format[SingleExperimentResult]
}

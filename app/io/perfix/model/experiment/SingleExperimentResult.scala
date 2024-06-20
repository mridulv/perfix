package io.perfix.model.experiment

import play.api.libs.json.{Format, Json}

case class SingleExperimentResult(overallQueryTime: Long,
                                  overallWriteTimeTaken: Long,
                                  numberOfCalls: Int,
                                  queryLatencies: Seq[PercentileLatency],
                                  writeLatencies: Seq[PercentileLatency])

case class PercentileLatency(percentile: Int, latency: Double)

object PercentileLatency {
  implicit val PercentileLatencyFormatter: Format[PercentileLatency] = Json.format[PercentileLatency]
}

object SingleExperimentResult {
  implicit val SingleExperimentResultFormatter: Format[SingleExperimentResult] = Json.format[SingleExperimentResult]
}

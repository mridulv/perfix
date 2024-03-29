package io.perfix.model

import play.api.libs.json.{Format, Json}

case class PerfixExperimentResult(overallQueryTime: Long,
                                  overallWriteTimeTaken: Long,
                                  numberOfCalls: Int,
                                  queryLatencies: Seq[PercentileLatency],
                                  writeLatencies: Seq[PercentileLatency])

object PerfixExperimentResult {
  implicit val ExperimentResultFormatter: Format[PerfixExperimentResult] = Json.format[PerfixExperimentResult]
}

case class PercentileLatency(percentile: Int, latency: Double)

object PercentileLatency {
  implicit val PercentileLatencyFormatter: Format[PercentileLatency] = Json.format[PercentileLatency]
}

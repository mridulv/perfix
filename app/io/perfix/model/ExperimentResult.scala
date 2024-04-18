package io.perfix.model

import play.api.libs.json.{Format, Json}

case class ExperimentResult(overallQueryTime: Long,
                            overallWriteTimeTaken: Long,
                            numberOfCalls: Int,
                            queryLatencies: Seq[PercentileLatency],
                            writeLatencies: Seq[PercentileLatency])

object ExperimentResult {
  implicit val ExperimentResultFormatter: Format[ExperimentResult] = Json.format[ExperimentResult]
}

case class PercentileLatency(percentile: Int, latency: Double)

object PercentileLatency {
  implicit val PercentileLatencyFormatter: Format[PercentileLatency] = Json.format[PercentileLatency]
}

package io.perfix.model

import play.api.libs.json.{Format, Json}

case class ExperimentResult(timeTaken: Long, numberOfCalls: Int, latencies: Seq[PercentileLatency])

object ExperimentResult {
  implicit val ExperimentResultFormatter: Format[ExperimentResult] = Json.format[ExperimentResult]
}

case class PercentileLatency(percentile: Int, latency: Double)

object PercentileLatency {
  implicit val PercentileLatencyFormatter: Format[PercentileLatency] = Json.format[PercentileLatency]
}

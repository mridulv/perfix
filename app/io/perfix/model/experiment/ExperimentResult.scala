package io.perfix.model.experiment

import play.api.libs.json.{Format, Json}

case class ExperimentResult(overallQueryTime: Long,
                            overallWriteTimeTaken: Long,
                            numberOfCalls: Int,
                            queryLatencies: Seq[PercentileLatency],
                            writeLatencies: Seq[PercentileLatency])

object ExperimentResult {
  implicit val ExperimentResultFormatter: Format[ExperimentResult] = Json.format[ExperimentResult]

  val TestExperimentResult: ExperimentResult = {
    val test = "{\"overallQueryTime\":5,\"overallWriteTimeTaken\":39,\"numberOfCalls\":13522,\"queryLatencies\":[{\"percentile\":5,\"latency\":1},{\"percentile\":10,\"latency\":2},{\"percentile\":25,\"latency\":3},{\"percentile\":50,\"latency\":3},{\"percentile\":75,\"latency\":5},{\"percentile\":90,\"latency\":6},{\"percentile\":95,\"latency\":7},{\"percentile\":99,\"latency\":9}],\"writeLatencies\":[{\"percentile\":5,\"latency\":39},{\"percentile\":10,\"latency\":39},{\"percentile\":25,\"latency\":39},{\"percentile\":50,\"latency\":39},{\"percentile\":75,\"latency\":39},{\"percentile\":90,\"latency\":39},{\"percentile\":95,\"latency\":39},{\"percentile\":99,\"latency\":39}]}"
    Json.parse(test).as[ExperimentResult]
  }
}

case class PercentileLatency(percentile: Int, latency: Double)

object PercentileLatency {
  implicit val PercentileLatencyFormatter: Format[PercentileLatency] = Json.format[PercentileLatency]
}

package io.perfix.model

import play.api.libs.json.{Json, Reads, Writes}

case class PerfixExperimentResult(id: Int)

object PerfixExperimentResult {
  implicit val PerfixExperimentResultWrites: Writes[PerfixExperimentResult] = Json.writes[PerfixExperimentResult]
  implicit val PerfixExperimentResultReads: Reads[PerfixExperimentResult] = Json.reads[PerfixExperimentResult]
}

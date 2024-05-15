package io.perfix.model.experiment

import play.api.libs.json.{Json, Reads, Writes}

case class ExperimentId(id: Int)

object ExperimentId {
  implicit val ExperimentIdWrites: Writes[ExperimentId] = Json.writes[ExperimentId]
  implicit val ExperimentIdReads: Reads[ExperimentId] = Json.reads[ExperimentId]
}

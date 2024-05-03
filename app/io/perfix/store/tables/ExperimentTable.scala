package io.perfix.store.tables

import io.perfix.model.{ExperimentId, ExperimentParams}
import play.api.libs.json.Json
import slick.jdbc.PostgresProfile.api._

case class ExperimentRow(id: Int, obj: String) {

  def toExperimentParams: ExperimentParams = {
    Json.parse(this.obj).as[ExperimentParams].copy(experimentId = Some(ExperimentId(id)))
  }

}

class ExperimentTable(tag: Tag) extends Table[ExperimentRow](tag, "experiment") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def obj = column[String]("obj")

  def * = (id, obj) <> ((ExperimentRow.apply _).tupled, ExperimentRow.unapply)
}

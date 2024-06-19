package io.perfix.db.tables

import io.perfix.model.experiment.{ExperimentId, ExperimentParams}
import play.api.libs.json.Json
import slick.jdbc.PostgresProfile.api._

case class ExperimentRow(id: Int, userEmail: String, obj: String) {

  def toExperimentParams: ExperimentParams = {
    Json.parse(this.obj).as[ExperimentParams].copy(experimentId = Some(ExperimentId(id)))
  }

}

class ExperimentTable(tag: Tag) extends Table[ExperimentRow](tag, "experiment") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def userEmail = column[String]("useremail")
  def obj = column[String]("obj")

  def * = (id, userEmail, obj) <> ((ExperimentRow.apply _).tupled, ExperimentRow.unapply)
}


package io.perfix.store.tables

import io.perfix.model.api.{DatasetId, DatasetParams}
import play.api.libs.json.Json
import slick.jdbc.PostgresProfile.api._

case class DatasetConfigRow(id: Int, userEmail: String, obj: String) {

  def toDatasetParams: DatasetParams = {
    Json.parse(this.obj).as[DatasetParams].copy(id = Some(DatasetId(id)))
  }

}

class DatasetConfigTable(tag: Tag) extends Table[DatasetConfigRow](tag, "datasetconfig") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def userEmail = column[String]("useremail")
  def obj = column[String]("obj")

  def * = (id, userEmail, obj) <> ((DatasetConfigRow.apply _).tupled, DatasetConfigRow.unapply)
}

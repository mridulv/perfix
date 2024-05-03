package io.perfix.store.tables

import io.perfix.model.{DatabaseConfigId, DatabaseConfigParams, ExperimentId, ExperimentParams}
import play.api.libs.json.Json
import slick.jdbc.PostgresProfile.api._

case class DatabaseConfigRow(id: Int, obj: String) {

  def toDatabaseConfigParams: DatabaseConfigParams = {
    Json.parse(this.obj).as[DatabaseConfigParams].copy(databaseConfigId = Some(DatabaseConfigId(id)))
  }

}

class DatabaseConfigTable(tag: Tag) extends Table[DatabaseConfigRow](tag, "databaseconfig") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def obj = column[String]("obj")

  def * = (id, obj) <> ((DatabaseConfigRow.apply _).tupled, DatabaseConfigRow.unapply)
}

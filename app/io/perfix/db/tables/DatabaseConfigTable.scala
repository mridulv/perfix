package io.perfix.db.tables

import io.perfix.model.api.{DatabaseConfigId, DatabaseConfigParams}
import play.api.libs.json.Json
import slick.jdbc.PostgresProfile.api._

case class DatabaseConfigRow(id: Int, userEmail: String, obj: String) {

  def toDatabaseConfigParams: DatabaseConfigParams = {
    Json.parse(this.obj).as[DatabaseConfigParams].copy(databaseConfigId = Some(DatabaseConfigId(id)))
  }

}

class DatabaseConfigTable(tag: Tag) extends Table[DatabaseConfigRow](tag, "databaseconfig") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def userEmail = column[String]("useremail")
  def obj = column[String]("obj")

  def * = (id, userEmail, obj) <> ((DatabaseConfigRow.apply _).tupled, DatabaseConfigRow.unapply)
}

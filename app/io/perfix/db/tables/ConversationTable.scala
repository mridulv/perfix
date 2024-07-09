package io.perfix.db.tables

import io.perfix.model.api.{UseCaseId, UseCaseParams}
import play.api.libs.json.Json
import slick.jdbc.PostgresProfile.api._

case class ConversationRow(id: Int, userEmail: String, obj: String) {

  def toConversationRow: UseCaseParams = {
    Json.parse(this.obj).as[UseCaseParams].copy(useCaseId = Some(UseCaseId(id)))
  }

}

class ConversationTable(tag: Tag) extends Table[ConversationRow](tag, "conversations") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def userEmail = column[String]("useremail")
  def obj = column[String]("obj")

  def * = (id, userEmail, obj) <> ((ConversationRow.apply _).tupled, ConversationRow.unapply)
}


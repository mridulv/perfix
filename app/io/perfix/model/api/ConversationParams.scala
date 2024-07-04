package io.perfix.model.api

import io.perfix.db.tables.ConversationRow
import play.api.libs.json.{Format, Json}

case class ConversationParams(conversationId: Option[ConversationId],
                              name: Option[String] = None,
                              conversationDetails: Option[ConversationDetails] = None,
                              createdAt: Option[Long] = None) {

  def toConversationRow(userEmail: String): ConversationRow = {
    conversationId match {
      case Some(id) =>
        ConversationRow(id = id.id, userEmail = userEmail, obj = Json.toJson(this).toString())
      case None =>
        ConversationRow(id = -1, userEmail = userEmail, obj = Json.toJson(this).toString())
    }
  }

}

case class ConversationDetails(messages: ConversationMessage)

case class ConversationMessage(user: String, message: String)

object ConversationParams {
  implicit val ConversationMessageFormatter: Format[ConversationMessage] = Json.format[ConversationMessage]
  implicit val ConversationDetailsFormatter: Format[ConversationDetails] = Json.format[ConversationDetails]
  implicit val ConversationParamsFormatter: Format[ConversationParams] = Json.format[ConversationParams]
}
package io.perfix.model.api

import io.cequence.openaiscala.domain.ChatRole.Assistant
import io.cequence.openaiscala.domain.{AssistantMessage, BaseMessage, ChatRole, SystemMessage, UserMessage}
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

  def addConversation(conversationMessage: ConversationMessage): ConversationParams = {
    conversationDetails match {
      case Some(conversation) => this.copy(conversationDetails = Some(conversation.addMessage(conversationMessage)))
      case None => this.copy(conversationDetails = Some(ConversationDetails(Seq(conversationMessage))))
    }
  }

}

case class ConversationDetails(messages: Seq[ConversationMessage]) {

  def addMessage(conversationMessage: ConversationMessage): ConversationDetails = {
    this.copy(messages = messages ++ Seq(conversationMessage))
  }

}

case class ConversationMessage(user: ChatRole, message: String) {

  def toBaseMessage: BaseMessage ={
    user match {
      case ChatRole.System => SystemMessage(message)
      case ChatRole.Assistant => AssistantMessage(message)
      case ChatRole.User => UserMessage(message)
      case _ => throw new RuntimeException("Invalid User")
    }
  }

}

object ConversationParams {
  implicit val ConversationMessageFormatter: Format[ConversationMessage] = Json.format[ConversationMessage]
  implicit val ConversationDetailsFormatter: Format[ConversationDetails] = Json.format[ConversationDetails]
  implicit val ConversationParamsFormatter: Format[ConversationParams] = Json.format[ConversationParams]
}
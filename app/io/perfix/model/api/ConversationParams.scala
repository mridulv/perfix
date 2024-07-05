package io.perfix.model.api

import io.cequence.openaiscala.domain.ChatRole.Assistant
import io.cequence.openaiscala.domain.{AssistantMessage, BaseMessage, ChatRole, SystemMessage, UserMessage}
import io.perfix.db.tables.ConversationRow
import io.perfix.model.api.ConversationState.ConversationState
import play.api.libs.json.{Format, Json}

case class ConversationParams(conversationId: Option[ConversationId],
                              name: Option[String] = None,
                              conversationDetails: Option[ConversationDetails] = None,
                              conversationState: Option[ConversationState] = Some(ConversationState.Created),
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

case class ConversationMessage(user: String, message: String) {

  def toBaseMessage: BaseMessage = {
    user match {
      case s if s == ChatRole.System.toString() => SystemMessage(message)
      case s if s == ChatRole.Assistant.toString() => AssistantMessage(message)
      case s if s == ChatRole.User.toString() => UserMessage(message)
      case _ => throw new RuntimeException("Invalid User")
    }
  }

}

object ConversationParams {
  implicit val ConversationMessageFormatter: Format[ConversationMessage] = Json.format[ConversationMessage]
  implicit val ConversationDetailsFormatter: Format[ConversationDetails] = Json.format[ConversationDetails]
  implicit val ConversationParamsFormatter: Format[ConversationParams] = Json.format[ConversationParams]
}
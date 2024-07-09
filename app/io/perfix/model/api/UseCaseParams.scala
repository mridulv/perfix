package io.perfix.model.api

import io.cequence.openaiscala.domain.{AssistantMessage, BaseMessage, ChatRole, SystemMessage, UserMessage}
import io.perfix.db.tables.UseCaseRow
import io.perfix.model.api.UseCaseState.UseCaseState
import play.api.libs.json.{Format, Json}

case class UseCaseParams(useCaseId: Option[UseCaseId],
                         name: Option[String] = None,
                         useCaseDetails: Option[UseCaseDetails] = None,
                         useCaseState: Option[UseCaseState] = Some(UseCaseState.Created),
                         createdAt: Option[Long] = None) {

  def toUseCaseRow(userEmail: String): UseCaseRow = {
    useCaseId match {
      case Some(id) =>
        UseCaseRow(id = id.id, userEmail = userEmail, obj = Json.toJson(this).toString())
      case None =>
        UseCaseRow(id = -1, userEmail = userEmail, obj = Json.toJson(this).toString())
    }
  }

  def addConversation(conversationMessage: ConversationMessage): UseCaseParams = {
    useCaseDetails match {
      case Some(conversation) => this.copy(useCaseDetails = Some(conversation.addMessage(conversationMessage)))
      case None => this.copy(useCaseDetails = Some(UseCaseDetails(Seq(conversationMessage))))
    }
  }

}

case class UseCaseDetails(messages: Seq[ConversationMessage]) {

  def addMessage(conversationMessage: ConversationMessage): UseCaseDetails = {
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

object UseCaseParams {
  implicit val ConversationMessageFormatter: Format[ConversationMessage] = Json.format[ConversationMessage]
  implicit val ConversationDetailsFormatter: Format[UseCaseDetails] = Json.format[UseCaseDetails]
  implicit val ConversationParamsFormatter: Format[UseCaseParams] = Json.format[UseCaseParams]
}
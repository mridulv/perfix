package io.perfix.manager

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.google.inject.{Inject, Singleton}
import io.cequence.openaiscala.domain.ChatRole
import io.cequence.openaiscala.service.OpenAIServiceFactory
import io.perfix.db.ConversationStore
import io.perfix.model.api.{ConversationId, ConversationMessage, ConversationParams, ConversationState}
import io.perfix.util.ConversationSystemPrompt.{CheckIfConversationCompletedMessage, CompletionConversationMessage, SystemConversationMessage}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}


@Singleton
class ConversationManager @Inject()(conversationStore: ConversationStore) {
  implicit val ec = ExecutionContext.global
  implicit val materializer = Materializer(ActorSystem())

  def create(conversationParams: ConversationParams): ConversationParams = {
    val createdConversationParams = conversationStore.create(conversationParams)
    val id = createdConversationParams.conversationId.map(_.id).getOrElse(-1)
    val updatedConversationParams = createdConversationParams.copy(
      name = Some(s"Untitled $id"),
      conversationState = Some(ConversationState.Created)
    )
    conversationStore.update(
      ConversationId(id),
      updatedConversationParams
    )
    updatedConversationParams
  }

  def get(conversationId: ConversationId): ConversationParams = {
    conversationStore
      .get(conversationId)
      .getOrElse(throw new RuntimeException(s"Invalid ${conversationId}"))
  }

  def converse(conversationId: ConversationId, message: String): String = {
    val conversationParams = get(conversationId)
    val updatedConversationParams = conversationParams.addConversation(
      ConversationMessage(ChatRole.User.toString(), message)
    )
    val conversationMessage: ConversationMessage = updatedConversationParams.conversationDetails match {
      case Some(conversation) => chatResponse(conversation.messages)
      case None => throw new RuntimeException(s"Invalid ConversationParams for id: ${conversationId}")
    }
    if (conversationMessage == CheckIfConversationCompletedMessage) {
      conversationStore.update(
        conversationId,
        updatedConversationParams
          .addConversation(conversationMessage)
          .copy(conversationState = Some(ConversationState.Completed))
      )
    } else {
      conversationStore.update(
        conversationId,
        updatedConversationParams
          .addConversation(conversationMessage)
          .copy(conversationState = Some(ConversationState.InProgress))
      )
    }
    conversationMessage.message
  }

  def list(): Seq[ConversationParams] = {
    conversationStore.list()
  }

  def delete(conversationId: ConversationId): Int = {
    conversationStore.delete(conversationId)
  }

  private def chatResponse(messages: Seq[ConversationMessage]): ConversationMessage = {
    val service = OpenAIServiceFactory()
    val allMessages = Seq(SystemConversationMessage) ++ messages
    val response = Await.result(service.createChatCompletion(allMessages.map(_.toBaseMessage)), Duration.Inf)
      .choices
      .head
      .message
      .content
    val assistantMessage = ConversationMessage(ChatRole.Assistant.toString(), response)
    if (checkIfConversationEnded(allMessages ++ Seq(assistantMessage))) {
      CompletionConversationMessage
    } else {
      assistantMessage
    }
  }

  private def checkIfConversationEnded(messages: Seq[ConversationMessage]): Boolean = {
    val service = OpenAIServiceFactory()
    val allMessages = Seq(CheckIfConversationCompletedMessage) ++ messages
    val response = Await.result(service.createChatCompletion(allMessages.map(_.toBaseMessage)), Duration.Inf)
      .choices
      .head
      .message
      .content
    response.strip().toLowerCase == "yes"
  }
}

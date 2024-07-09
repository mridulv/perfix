package io.perfix.manager

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.google.inject.{Inject, Singleton}
import io.cequence.openaiscala.domain.ChatRole
import io.cequence.openaiscala.service.OpenAIServiceFactory
import io.perfix.db.UseCaseStore
import io.perfix.model.api.{UseCaseId, ConversationMessage, UseCaseParams, UseCaseState}
import io.perfix.util.ConversationSystemPrompt.{CheckIfConversationCompletedMessage, CompletionConversationMessage, SystemConversationMessage}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}


@Singleton
class UseCaseManager @Inject()(useCaseStore: UseCaseStore) {
  implicit val ec = ExecutionContext.global
  implicit val materializer = Materializer(ActorSystem())

  def create(dbExplorerParams: UseCaseParams): UseCaseParams = {
    val createdConversationParams = useCaseStore.create(dbExplorerParams)
    val id = createdConversationParams.useCaseId.map(_.id).getOrElse(-1)
    val updatedConversationParams = createdConversationParams.copy(
      name = Some(s"Untitled $id"),
      useCaseState = Some(UseCaseState.Created)
    )
    useCaseStore.update(
      UseCaseId(id),
      updatedConversationParams
    )
    updatedConversationParams
  }

  def get(conversationId: UseCaseId): UseCaseParams = {
    useCaseStore
      .get(conversationId)
      .getOrElse(throw new RuntimeException(s"Invalid ${conversationId}"))
  }

  def converse(conversationId: UseCaseId, message: String): String = {
    val conversationParams = get(conversationId)
    val updatedConversationParams = conversationParams.addConversation(
      ConversationMessage(ChatRole.User.toString(), message)
    )
    val conversationMessage: ConversationMessage = updatedConversationParams.useCaseDetails match {
      case Some(conversation) => chatResponse(conversation.messages)
      case None => throw new RuntimeException(s"Invalid ConversationParams for id: ${conversationId}")
    }
    if (conversationMessage == CheckIfConversationCompletedMessage) {
      useCaseStore.update(
        conversationId,
        updatedConversationParams
          .addConversation(conversationMessage)
          .copy(useCaseState = Some(UseCaseState.Completed))
      )
    } else {
      useCaseStore.update(
        conversationId,
        updatedConversationParams
          .addConversation(conversationMessage)
          .copy(useCaseState = Some(UseCaseState.InProgress))
      )
    }
    conversationMessage.message
  }

  def list(): Seq[UseCaseParams] = {
    useCaseStore.list()
  }

  def delete(conversationId: UseCaseId): Int = {
    useCaseStore.delete(conversationId)
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

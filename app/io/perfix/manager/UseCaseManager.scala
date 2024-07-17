package io.perfix.manager

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.google.inject.{Inject, Singleton}
import io.cequence.openaiscala.domain.ChatRole
import io.cequence.openaiscala.service.OpenAIServiceFactory
import io.perfix.db.UseCaseStore
import io.perfix.model.{EntityFilter, UseCaseFilter}
import io.perfix.model.api.{ConversationMessage, UseCaseId, UseCaseParams, UseCaseState}
import io.perfix.util.ConversationSystemPrompt.{CheckIfConversationCompletedMessage, CompletionConversationMessage, SystemConversationMessage}
import play.api.Configuration

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}


@Singleton
class UseCaseManager @Inject()(useCaseStore: UseCaseStore,
                               datasetManager: DatasetManager,
                               databaseConfigManager: DatabaseConfigManager,
                               experimentManager: ExperimentManager,
                               configuration: Configuration) {

  private val APP_URL: String = configuration.get[String]("app.baseUrl")

  implicit val ec = ExecutionContext.global
  implicit val materializer = Materializer(ActorSystem())

  def create(useCaseParams: UseCaseParams): UseCaseParams = {
    val createdUseCaseParams = useCaseStore.create(useCaseParams)
    val id = createdUseCaseParams.useCaseId.map(_.id).getOrElse(-1)
    val updatedUseCaseParams = createdUseCaseParams.copy(
      name = Some(s"Untitled $id"),
      useCaseState = Some(UseCaseState.Created)
    )
    useCaseStore.update(
      UseCaseId(id),
      updatedUseCaseParams
    )
    updatedUseCaseParams
  }

  def get(useCaseId: UseCaseId): UseCaseParams = {
    useCaseStore
      .get(useCaseId)
      .getOrElse(throw new RuntimeException(s"Invalid ${useCaseId}"))
  }

  def converse(useCaseId: UseCaseId, message: String): String = {
    val useCaseParams = get(useCaseId)
    val updatedUseCaseParams = useCaseParams.addConversation(
      ConversationMessage(ChatRole.User.toString(), message)
    )
    val responseConversationMessage: ConversationMessage = updatedUseCaseParams.useCaseDetails match {
      case Some(conversation) => wrappedResponse(conversation.messages)
      case None => throw new RuntimeException(s"Invalid ConversationParams for id: ${useCaseId}")
    }
    if (responseConversationMessage == CompletionConversationMessage) {
      val endConversationMessage = handleEndConversation(useCaseParams)
      useCaseStore.update(
        useCaseId,
        updatedUseCaseParams.copy(useCaseState = Some(UseCaseState.Completed))
      )
    } else {
      useCaseStore.update(
        useCaseId,
        updatedUseCaseParams.addConversation(responseConversationMessage).copy(useCaseState = Some(UseCaseState.InProgress))
      )
    }
    responseConversationMessage.message
  }

  def all(entityFilters: Seq[EntityFilter]): Seq[UseCaseParams] = {
    val allUseCases = useCaseStore.list()
    val useCaseFilters = entityFilters.collect {
      case df: UseCaseFilter => df
    }
    useCaseFilters.foldLeft(allUseCases) { (useCaseParams, filter) =>
      useCaseParams.filter(d => filter.filter(d))
    }
  }

  def delete(useCaseId: UseCaseId): Int = {
    useCaseStore.delete(useCaseId)
  }

  private def handleEndConversation(useCaseParams: UseCaseParams): ConversationMessage = {
    val conversationMessages = useCaseParams.useCaseDetails.map(_.messages).getOrElse(Seq.empty)
    val useCaseConversationParser = new UseCaseConversationParser(conversationMessages)
    val (datasetId, databaseConfigId, experimentId) = useCaseConversationParser.init(datasetManager, databaseConfigManager, experimentManager)
    ConversationMessage(
      ChatRole.System.toString(),
      s"We have created a benchmark run for you given all the inputs in the chat. Link: ${APP_URL}/experiment/${experimentId}"
    )
  }

  private def wrappedResponse(messages: Seq[ConversationMessage]): ConversationMessage = {
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
    val allMessages = messages ++ Seq(CheckIfConversationCompletedMessage)
    val response = Await.result(service.createChatCompletion(allMessages.map(_.toBaseMessage)), Duration.Inf)
      .choices
      .head
      .message
      .content
    response.strip().toLowerCase == "yes"
  }
}

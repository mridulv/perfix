package io.perfix.manager

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.google.inject.{Inject, Singleton}
import io.cequence.openaiscala.domain.ChatRole
import io.cequence.openaiscala.service.OpenAIServiceFactory
import io.perfix.db.ConversationStore
import io.perfix.model.api.{ConversationId, ConversationMessage, ConversationParams}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}


@Singleton
class ConversationManager @Inject()(conversationStore: ConversationStore) {
  implicit val ec = ExecutionContext.global
  implicit val materializer = Materializer(ActorSystem())

  def create(conversationParams: ConversationParams): ConversationParams = {
    val createdConversationParams = conversationStore.create(conversationParams)
    val id = createdConversationParams.conversationId.map(_.id).getOrElse(-1)
    val updatedConversationParams = createdConversationParams.copy(name = Some(s"Untitled $id"))
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
    val assistantMessage: String = updatedConversationParams.conversationDetails match {
      case Some(conversation) => chatResponse(conversation.messages)
      case None => throw new RuntimeException(s"Invalid ConversationParams for id: ${conversationId}")
    }
    conversationStore.update(
      conversationId,
      updatedConversationParams.addConversation(
        ConversationMessage(ChatRole.Assistant.toString(), assistantMessage)
      )
    )
    assistantMessage
  }

  def list(): Seq[ConversationParams] = {
    conversationStore.list()
  }

  def delete(conversationId: ConversationId): Int = {
    conversationStore.delete(conversationId)
  }

  private def chatResponse(messages: Seq[ConversationMessage]): String = {
    val systemMessage = ConversationMessage(
      ChatRole.System.toString(),
      "Assist users with database performance benchmarking. It guides users in defining their use-case, understanding their data schema, and choosing the best database for their needs. It probes users about their data schema, column types, and specific queries they wish to run. It suggests the most suitable database, helps define their queries and workload in SQL, and assists in understanding if they want to run a benchmark against different databases. If users choose to run an experiment, it asks about the experiment parameters such as duration, data size, and concurrent read/write operations. Additionally, it collects database configuration details for AWS deployment, including memory and CPU specifications. The GPT ensures it doesn't overload the user with too many questions at once, asking them one by one and pacing the interaction to avoid overwhelming the user. For platform owners, the GPT constructs a JSON object of tables, columns, and column types. It determines if users want to run experiments, identifies the databases of interest, and gathers experiment configuration details, including time, concurrent reads, and writes, as well as database configurations. The GPT only responds to prompts starting with 'user:' or 'platform:'. If the prompt is 'user:', it does not provide analysis or code-related responses."
    )
    val service = OpenAIServiceFactory()
    val allMessages = Seq(systemMessage) ++ messages
    Await.result(service.createChatCompletion(allMessages.map(_.toBaseMessage)), Duration.Inf)
      .choices
      .head
      .message
      .content
  }
}

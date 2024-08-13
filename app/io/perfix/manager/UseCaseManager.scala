package io.perfix.manager

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.google.inject.{Inject, Singleton}
import io.cequence.openaiscala.domain.ChatRole
import io.cequence.openaiscala.service.OpenAIServiceFactory
import io.perfix.conversations.ConversationCompiler.allFieldsCompilationFailure
import io.perfix.conversations.{CompilationError, ConversationCompiler, UseCaseConversationParser}
import io.perfix.db.UseCaseStore
import io.perfix.model.{EntityFilter, UseCaseFilter}
import io.perfix.model.api.{ConversationMessage, UseCaseId, UseCaseParams, UseCaseState}
import io.perfix.stores.Database
import io.perfix.util.ConversationSystemPrompt
import io.perfix.util.ConversationSystemPrompt.SystemConversationMessage
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
    if (useCaseParams.useCaseState.getOrElse(UseCaseState.Created) != UseCaseState.Completed) {
      val updatedUseCaseParams = useCaseParams.addConversation(
        ConversationMessage(ChatRole.User.toString(), message)
      )
      val conversationMessage: ConversationMessage = updatedUseCaseParams.useCaseDetails match {
        case Some(conversation) =>
          val (response, compilationErrors) = findCompilationIssues(conversation.messages)
          val overallMessages = conversation.messages
          if (compilationErrors.isEmpty) {
            val endConversationMessage = handleEndConversation(response)
            useCaseStore.update(
              useCaseId,
              updatedUseCaseParams
                .addConversation(endConversationMessage)
                .copy(useCaseState = Some(UseCaseState.Completed))
            )
            endConversationMessage
          } else {
            println(s"There were ${compilationErrors.size} compilation errors. These are the compilation errors: \n\n${compilationErrors.map(_.compilationError).mkString("\n")}. " +
              s"\n\n" +
              s"These are the compilation errors in the json object response")
            val finalResponse = getAssistantResponse(overallMessages)
            useCaseStore.update(
              useCaseId,
              updatedUseCaseParams.addConversation(finalResponse)copy(useCaseState = Some(UseCaseState.InProgress))
            )
            finalResponse
          }
        case None => throw new RuntimeException(s"Invalid ConversationParams for id: ${useCaseId}")
      }
      conversationMessage.message
    } else {
      throw new RuntimeException(s"Conversation is completed for ${useCaseId}")
    }
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

  private def handleEndConversation(response: String): ConversationMessage = {
    val useCaseConversationParser = new UseCaseConversationParser(response)
    val (datasetId, databaseConfigId, experimentId) = useCaseConversationParser.init(datasetManager, databaseConfigManager, experimentManager)
    ConversationMessage(
      ChatRole.System.toString(),
      s"We have created a benchmark run for you given all the inputs in the chat. Link: ${APP_URL}/experiment/${experimentId.id}"
    )
  }

  private def getAssistantResponse(messages: Seq[ConversationMessage]): ConversationMessage = {
    val service = OpenAIServiceFactory()
    val allMessages = Seq(SystemConversationMessage) ++ messages
    val response = Await.result(service.createChatCompletion(allMessages.map(_.toBaseMessage)), Duration.Inf)
      .choices
      .head
      .message
      .content
    ConversationMessage(ChatRole.Assistant.toString(), response)
  }

  private def findCompilationIssues(conversationMessages: Seq[ConversationMessage]): (String, Seq[CompilationError]) = {
    val service = OpenAIServiceFactory()
    val jsonMessage = ConversationMessage(
      ChatRole.System.toString(),
      """Given the user inputs before this, can you create a json object of the response in this format: {"schema": [{"fieldName" : "$fieldName", "fieldType": "$fieldType"}], "databaseType": [$database1, $database2],  "query": $query, "filteredRows": $filteredRows, "experiment_time_in_seconds": $experiment_time_in_seconds, "total_rows": $total_rows, "concurrent_reads_rate": $concurrent_reads_rate, "concurrent_writes_rate": $concurrent_writes_rate }. Note if there is no corresponding value defined by the user till now, leave the field as null and do not assign any default values. Also the response should be a valid json object
        | Make sure of the following things
        | - $fieldType should always be among (long, bool, int, string, double)
        | - $query should be in SQL format. Also make sure the variable names start with {{ and ends with }}. Variable names in the SQL should have the same name as the fieldNames.
        | - $database1 / $database2 and so on - Databases should be among (""" + Database.allDatabases.map(_.name.toString).mkString(",") + """)
        | - $filteredRows should be in Int
        | - $experiment_time_in_seconds should be in Int
        | - $total_rows should be in Int
        | - $concurrent_reads_rate should be in Int
        | - $concurrent_writes_rate should be in Int
        |
        | Do Make sure that the format of the json object is the same as mentioned above.
        | Also please consider the compilation failures in the previous json objects to return the json object.
        |
        |""".stripMargin,
      isHidden = Some(true)
    )
    val toBeSend = Seq(ConversationSystemPrompt.SystemConversationMessage) ++ conversationMessages ++ Seq(jsonMessage)
    val response = Await.result(service.createChatCompletion(toBeSend.map(_.toBaseMessage)), Duration.Inf)
      .choices
      .head
      .message
      .content
    println(s"Assistant: $response")
    try {
      val compilationErrors = ConversationCompiler.compile(response)
      (response, compilationErrors)
    } catch {
      case e: Exception => (response, allFieldsCompilationFailure())
    }
  }
}

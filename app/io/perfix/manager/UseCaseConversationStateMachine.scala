package io.perfix.manager

import akka.actor.ActorSystem
import akka.stream.Materializer
import io.cequence.openaiscala.domain.ChatRole
import io.cequence.openaiscala.service.OpenAIServiceFactory
import io.perfix.model.api.ConversationMessage
import io.perfix.util.ConversationSystemPrompt
import play.api.libs.json.{JsDefined, JsNull, JsValue, Json}

import scala.collection.mutable.ListBuffer
import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration.Duration

object UseCaseConversationStateMachine {
  val requiredKeys = Seq(
    "schema",
    "databaseType",
    "queries",
    "filteredRows",
    "experiment_time_in_seconds",
    "total_rows",
    "concurrent_reads_rate",
    "concurrent_writes_rate"
  )

  def init(): Unit = {
    conversation()
  }

  private def confirmationAtEveryCheck(messages: Seq[ConversationMessage]): Seq[String] = {
    implicit val ec = ExecutionContext.global
    implicit val materializer = Materializer(ActorSystem())
    val service = OpenAIServiceFactory()
    val conversationMessage = ConversationMessage(
      ChatRole.System.toString(),
      """Given the user inputs, can you create a json object of the response in this format: {"schema": [{"fieldName" : "$fieldName", "fieldType": "$fieldType"}], "databaseType": [$database1, $database2],  "queries": [{"query" : $query1}, {"query" : $query2}], "filteredRows": $filteredRows, "experiment_time_in_seconds": $experiment_time_in_seconds, "total_rows": $total_rows, "concurrent_reads_rate": $concurrent_reads_rate, "concurrent_writes_rate": $concurrent_writes_rate }. Note if there is no corresponding value defined by the user till now, leave the field as null and do not assign any default values. Also the response should be a valid json object""".stripMargin
    )
    val toBeSend = (Seq(ConversationSystemPrompt.SystemConversationMessage) ++ messages ++ Seq(conversationMessage)).map(_.toBaseMessage)
    val response = Await.result(service.createChatCompletion(toBeSend), Duration.Inf)
      .choices
      .head
      .message
      .content
    println(s"Assistant: $response")
    try {
      val json = Json.parse(response)
      val fields = checkCompilation(json)
      fields
    } catch {
      case e: Exception => {
        println(e.toString)
        requiredKeys
      }
    }
  }

  private def checkCompilation(jsValue: JsValue): Seq[String] = {
    def isNonNull(key: String): Boolean = (jsValue \ key) match {
      case JsDefined(JsNull) => false
      case JsDefined(_) => true
      case _ => false
    }

    requiredKeys.filterNot(isNonNull)
  }

  private def conversation(): Unit = {
    implicit val ec = ExecutionContext.global
    implicit val materializer = Materializer(ActorSystem())
    val service = OpenAIServiceFactory()
    val messages: ListBuffer[ConversationMessage] = ListBuffer.empty
    var isConversationComplete = false
    while (!isConversationComplete) {
      println("User :")
      val userResponse = scala.io.StdIn.readLine()
      val userConversationMessage = ConversationMessage(ChatRole.User.toString(), userResponse)
      messages.append(userConversationMessage)

      val fields = confirmationAtEveryCheck(messages.toSeq)
      println(s"Compilation Failure: ${fields.mkString(",")} are still empty. Need to ask user questions for filling the values for these fields")
      val compilationMessage = ConversationMessage(ChatRole.Assistant.toString(), s"Compilation Failure: ${fields.mkString(",")} are still empty. Need to ask user questions for filling the values for these fields")
      messages.append(compilationMessage)
      isConversationComplete = fields.isEmpty

      val overallConversation = Seq(ConversationSystemPrompt.SystemConversationMessage) ++ messages.toSeq
      val response = Await.result(service.createChatCompletion(overallConversation.map(_.toBaseMessage)), Duration.Inf)
        .choices
        .head
        .message
        .content
      println(s"System : ${response}")
      messages.append(ConversationMessage(ChatRole.System.toString(), response))
    }
  }


}

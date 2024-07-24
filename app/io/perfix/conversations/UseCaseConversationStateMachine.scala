package io.perfix.conversations

import akka.actor.ActorSystem
import akka.stream.Materializer
import io.cequence.openaiscala.domain.ChatRole
import io.cequence.openaiscala.service.OpenAIServiceFactory
import io.perfix.model.api.{ConversationMessage, UseCaseParams}
import io.perfix.util.ConversationSystemPrompt
import play.api.libs.json.{JsDefined, JsNull, JsValue, Json}

import java.sql.{DriverManager, ResultSet}
import scala.collection.mutable.ListBuffer
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}
import scala.util.matching.Regex

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

  def resultSetToSeqMap(resultSet: ResultSet): Seq[Map[String, Any]] = {
    val metaData = resultSet.getMetaData
    val columnCount = metaData.getColumnCount
    val buffer = scala.collection.mutable.Buffer[Map[String, Any]]()

    while (resultSet.next()) {
      val row = (1 to columnCount).map { i =>
        metaData.getColumnName(i) -> resultSet.getObject(i)
      }.toMap
      buffer += row
    }

    buffer.toSeq
  }

  def main(args: Array[String]): Unit = {

    Class.forName("org.postgresql.Driver").newInstance
    val connection = DriverManager.getConnection("jdbc:postgresql://localhost/mydb", "myuser", "mypassword")
    val statement = connection.createStatement()

    val result = statement.executeQuery("select * from usecases;")
    val mapping = resultSetToSeqMap(result)
    val res = mapping.sortBy(e => e("id").toString.toInt).reverse.head
    val useCaseParams = Json.parse(res("obj").toString).as[UseCaseParams]

    var state: State = ConversationCompleted
    var context: Context = Context(ConversationCompleted, useCaseParams.useCaseDetails.get.messages)
    while (state != OverallConversationCompleted) {
      val res = transition(state, context)
      state = res._1
      context = res._2
    }
    println("complete")
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

  private def conversation(): Seq[ConversationMessage] = {
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
    messages.toSeq
  }

  private def checkFieldsCompilation(json: JsValue): Seq[String] = {

    val sqlPattern: Regex = """(?i)^\s*(SELECT|INSERT|UPDATE|DELETE)\s+.*""".r
    try {
      val queriesResult = (json \ "query").validate[String].get
      if (sqlPattern.findFirstIn(queriesResult).isDefined) {
        Seq.empty
      } else {
        println(s"Failed to parse queries:")
        Seq("query")
      }
    } catch {
      case e: Exception =>
        println(s"Failed to parse queries:")
        Seq("query")
    }
  }

  private def conversationForCompilationSuccess(conversationMessages: Seq[ConversationMessage]): Unit = {
    implicit val ec = ExecutionContext.global
    implicit val materializer = Materializer(ActorSystem())
    val service = OpenAIServiceFactory()
    var jsonMessage = ConversationMessage(
      ChatRole.System.toString(),
      """Given the user inputs before this, can you create a json object of the response in this format: {"schema": [{"fieldName" : "$fieldName", "fieldType": "$fieldType"}], "databaseType": [$database1, $database2],  "query": $query, "filteredRows": $filteredRows, "experiment_time_in_seconds": $experiment_time_in_seconds, "total_rows": $total_rows, "concurrent_reads_rate": $concurrent_reads_rate, "concurrent_writes_rate": $concurrent_writes_rate }. Note if there is no corresponding value defined by the user till now, leave the field as null and do not assign any default values. Also the response should be a valid json object
        | Make sure of the following things
        | - $fieldType should always be among (long, bool, int, string, double)
        | - $query should be in SQL format. Also make sure the variable names start with {{ and ends with }}. Variable names should the same exact names as the column name on which the operation is applied. Make sure variable name is the same as the column name.
        | - $filteredRows should be in Int
        | - $experiment_time_in_seconds should be in Int
        | - $total_rows should be in Int
        | - $concurrent_reads_rate should be in Int
        | - $concurrent_writes_rate should be in Int
        |
        | Do Make sure that the format of the json object is the same as mentioned above.
        |
        |""".stripMargin
    )
    var toBeSend = Seq(ConversationSystemPrompt.SystemConversationMessage) ++ conversationMessages ++ Seq(jsonMessage)
    var compilationErrors: Seq[CompilationError] = Seq.empty
    var continue: Boolean = true

    while (continue) {
      val response = Await.result(service.createChatCompletion(toBeSend.map(_.toBaseMessage)), Duration.Inf)
        .choices
        .head
        .message
        .content
      println(s"Assistant: $response")
      try {
        val json = Json.parse(response)
        compilationErrors = ConversationCompiler.compile(response)
        val compilationMessage = ConversationMessage(ChatRole.Assistant.toString(), s"There were ${compilationErrors.size} compilation errors. These are the compilation errors: ${compilationErrors.map(_.compilationError).mkString("\n")}. " +
          s"\n\n" +
          s"Based on these compilation errors emit an appropriate json object addressing the json object.")
        println(compilationMessage)
        toBeSend = toBeSend ++ Seq(compilationMessage)
      } catch {
        case e: Exception => {
          println(e.toString)
          val compilationMessage = ConversationMessage(ChatRole.Assistant.toString(), s"Compilation Failure: json generated for the response was not an actual json")
          toBeSend = toBeSend ++ Seq(compilationMessage)
        }
      }
      if (compilationErrors.nonEmpty) {
        continue = true
      } else {
        continue = false
      }
    }
  }

  def transition(state: State, context: Context): (State, Context) = {
    state match {
      case ConversationStarted =>
        val overallMessages = conversation()
        context.copy(messages = overallMessages)
        (ConversationCompleted, context)

      case ConversationCompleted =>
        conversationForCompilationSuccess(context.messages)
        (OverallConversationCompleted, context)

      case OverallConversationCompleted =>
        println("Final message to the user: Thank you for chatting with us!")
        (OverallConversationCompleted, context)

      case _ => (state, context)
    }
  }


}

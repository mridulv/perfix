package io.perfix.conversations

import akka.actor.ActorSystem
import akka.stream.Materializer
import io.cequence.openaiscala.domain.ChatRole
import io.cequence.openaiscala.service.OpenAIServiceFactory
import io.perfix.model.{BooleanValueType, ColumnDescription, ColumnType, NumericType, TextType}
import io.perfix.model.api.{ConversationMessage, Field, SqlQuery}
import io.perfix.model.store.StoreType
import io.perfix.model.store.StoreType.StoreType
import net.sf.jsqlparser.JSQLParserException
import net.sf.jsqlparser.parser.CCJSqlParserUtil
import play.api.libs.json.{JsArray, JsError, JsSuccess, JsValue, Json}

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration.Duration

object ConversationCompiler {
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

  def compile(response: String): Seq[CompilationError] = {
    try {
      val json = Json.parse(response)
      Seq(
        checkCompilationForSchema(json),
        //checkCompilationForDatabaseType(json),
        checkCompilationForExperimentParamsFields(json),
        checkCompilationForQuery(json)
      ).flatten
    } catch {
      case e: Exception => allFieldsCompilationFailure()
    }
  }

  def allFieldsCompilationFailure(): Seq[CompilationError] = {
    requiredKeys.map { key =>
      CompilationError(key, s"Please create a valid json object in the response. Not able to parse: ${key} from the json object")
    }
  }

  private def checkCompilationForSchema(json: JsValue): Option[CompilationError] = {
    val result = (json \ "schema").validate[Seq[Field]]
    result match {
      case JsSuccess(ok, _) => None
      case JsError(errors) => Some(CompilationError("schema", "Failed to parse \"schema\" field. Expectation is that schema field should be of this format: [{\"fieldName\" : \"$fieldName\", \"fieldType\": \"$fieldType\"}]"))
    }
  }

  private def checkCompilationForDatabaseType(json: JsValue): Option[CompilationError] = {
    val result = (json \ "databaseType").validate[Seq[StoreType]]
    result match {
      case JsSuccess(_, _) => None
      case JsError(errors) => Some(CompilationError("databaseType", s"Failed to parse \"databaseType\" field. Expectation is that databaseType should contain values among: ${StoreType.values.map(_.toString)}"))
    }
  }

  private def checkCompilationForExperimentParamsFields(json: JsValue): Seq[CompilationError] = {
    def parseField(fieldName: String): Option[CompilationError] = {
      val result = (json \ fieldName).validate[Int]
      result match {
        case JsSuccess(_, _) => None
        case JsError(errors) => Some(CompilationError(fieldName, s"Failed to parse \"$fieldName\" field. Expectation is that $fieldName should be in Int"))
      }
    }

    Seq(
      "filteredRows",
      "experiment_time_in_seconds",
      "total_rows",
      "concurrent_reads_rate",
      "concurrent_writes_rate"
    ).flatMap(f => parseField(f))
  }

  private def checkCompilationForQuery(json: JsValue): Option[CompilationError] = {
    (json \ "schema").validate[List[Field]].map(_.toList).asOpt.flatMap { fields =>
      val columnsDescriptions = fields.map { field =>
        val columnType: ColumnType = field.fieldType.toLowerCase match {
          case "string" => TextType()
          case "integer" => NumericType(None)
          case "boolean" => BooleanValueType()
          case "double" => NumericType(None)
          case "long" => NumericType(None)
        }
        ColumnDescription(field.fieldName, columnType)
      }

      val result = (json \ "query").validate[String]
      result match {
        case JsSuccess(query, _) =>
          validateSQLQuery(query, columnsDescriptions)
          None
        case JsError(errors) => Some(CompilationError("", s"Failed to parse \"query\" field. " +
          s"Expectation is that the \"query\" field value should be  \n " +
          s"1) A valid sql query  \n " +
          s"2) all the variables should be jinja variables \n "))
      }
    }
  }

  private def validateSQLQuery(query: String, columnDescriptions: Seq[ColumnDescription]): Unit = {
    var success = false
    var attempts = 0
    implicit val ec = ExecutionContext.global
    implicit val materializer = Materializer(ActorSystem())
    val service = OpenAIServiceFactory()
    val sqlQuery = SqlQuery(query).toSqlDBQuery.resolve(columnDescriptions.map { c => (c.columnName, c.columnType.getValue)}.toMap)
    val prompt = s"""Can we have the jinja variables name same as the column name on which the operation is applied in this sql query
                   |        "$query"
                   |        Response should just be a valid sql query with jinja variables""".stripMargin
    while (!success && attempts < 10) {
      attempts += 1
      try {
        val response = Await.result(service.createChatCompletion(Seq(ConversationMessage(ChatRole.System.toString(), prompt).toBaseMessage)), Duration.Inf)
          .choices
          .head
          .message
          .content
        val sql = CCJSqlParserUtil.parse(response)
        success = true
      } catch {
        case e: JSQLParserException => Some(CompilationError("", s"Invalid sql query: $query"))
      }
    }
  }


}

case class CompilationError(field: String, issue: String) {

  def compilationError: String = {
    s"- $field parsing failed. Issue was: $issue"
  }

}
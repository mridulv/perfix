package io.perfix.conversations

import io.perfix.model.{BooleanValueType, ColumnDescription, ColumnType, NumericType, TextType}
import io.perfix.model.api.{Field, SqlQuery}
import io.perfix.model.store.StoreType
import io.perfix.model.store.StoreType.StoreType
import net.sf.jsqlparser.JSQLParserException
import net.sf.jsqlparser.parser.CCJSqlParserUtil
import play.api.libs.json.{JsArray, JsError, JsSuccess, JsValue, Json}

object ConversationCompiler {

  def compile(response: String): Seq[CompilationError] = {
    val json = Json.parse(response)
    Seq(
      checkCompilationForSchema(json),
      //checkCompilationForDatabaseType(json),
      checkCompilationForExperimentParamsFields(json),
      checkCompilationForQuery(json)
    ).flatten
  }

  private def checkCompilationForSchema(json: JsValue): Option[CompilationError] = {
    val result = (json \ "schema").validate[Seq[Field]]
    result match {
      case JsSuccess(ok, _) => None
      case JsError(errors) => Some(CompilationError("schema", "Failed to parse schema field as per the defined structure"))
    }
  }

  private def checkCompilationForDatabaseType(json: JsValue): Option[CompilationError] = {
    val result = (json \ "databaseType").validate[Seq[StoreType]]
    result match {
      case JsSuccess(_, _) => None
      case JsError(errors) => Some(CompilationError("databaseType", s"Failed to parse databaseType. Expectation is that databaseType should contain values among: ${StoreType.values.map(_.toString)}"))
    }
  }

  private def checkCompilationForExperimentParamsFields(json: JsValue): Seq[CompilationError] = {
    def parseField(fieldName: String): Option[CompilationError] = {
      val result = (json \ fieldName).validate[Int]
      result match {
        case JsSuccess(_, _) => None
        case JsError(errors) => Some(CompilationError(fieldName, s"Failed to parse $fieldName. Expectation is that $fieldName should be in Int"))
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
          val sqlQuery = SqlQuery(query).toSqlDBQuery.resolve(columnsDescriptions.map { c => (c.columnName, c.columnType.getValue)}.toMap)
          try {
            CCJSqlParserUtil.parse(sqlQuery.sql)
            None
          } catch {
            case e: JSQLParserException =>
              Some(CompilationError("", s"Invalid sql query: $query"))
          }
        case JsError(errors) => Some(CompilationError("", s"Failed to parse databases. Expectation is that databaseType should contain values among: ${StoreType.values.map(_.toString)}"))
      }
    }
  }


}

case class CompilationError(field: String, issue: String) {

  def compilationError: String = {
    s"$field compilation failed. Issue was: $issue"
  }

}
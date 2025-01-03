package io.perfix.model.api

import io.perfix.query.SqlDBQuery
import play.api.libs.json.{Format, Json}

case class Field(fieldName: String, fieldType: String)

object Field {
  implicit val fieldReadsFormatter: Format[Field] = Json.format[Field]
}

case class ExperimentConfig(rows: Int,
                            num_writes_per_minute: Int,
                            reads_per_minute: Int)

object ExperimentConfig {
  implicit val ExperimentConfigFormatter: Format[ExperimentConfig] = Json.format[ExperimentConfig]
}

case class SqlQueries(queries: Seq[SqlQuery])

case class SqlQuery(query: String) {

  def toSqlDBQuery: SqlDBQuery = {
    SqlDBQuery(query)
  }

}

object SqlQueries {
  implicit val SqlQueryFormatter: Format[SqlQuery] = Json.format[SqlQuery]
  implicit val SqlQueriesFormatter: Format[SqlQueries] = Json.format[SqlQueries]
}
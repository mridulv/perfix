package io.perfix.model.api

import play.api.libs.json.{Format, Json}

case class Field(fieldName: String, fieldType: String)

object Field {
  implicit val fieldReadsFormatter: Format[Field] = Json.format[Field]
}

case class ExperimentConfig(rows: Int,
                            experiment_time_in_seconds: Int,
                            num_writes_per_minute: Int,
                            reads_per_minute: Int)

object ExperimentConfig {
  implicit val ExperimentConfigFormatter: Format[ExperimentConfig] = Json.format[ExperimentConfig]
}

case class SqlQueries(queries: Seq[SqlQuery])

case class SqlQuery(query: String)

object SqlQueries {
  implicit val SqlQueriesFormatter: Format[SqlQueries] = Json.format[SqlQueries]
  implicit val SqlQueryFormatter: Format[SqlQuery] = Json.format[SqlQuery]
}
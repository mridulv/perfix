package io.perfix.conversations

import io.perfix.model.api.Field
import play.api.libs.json.{Format, Json}

case class ExperimentRun(schema: Seq[Field],
                         databaseType: Seq[String],
                         query: String,
                         total_rows: Int,
                         concurrent_reads_rate: Int,
                         concurrent_writes_rate: Int,
                         filteredRows: Int)

object ExperimentRun {
  implicit val ExperimentRunFormatter: Format[ExperimentRun] = Json.format[ExperimentRun]
}

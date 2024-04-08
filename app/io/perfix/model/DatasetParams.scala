package io.perfix.model

import play.api.libs.json.{Format, Json}

case class DatasetParams(rows: Int, columns: Seq[ColumnDescription])

object DatasetParams {
  implicit val DatasetParamsFormatter: Format[DatasetParams] = Json.format[DatasetParams]
}

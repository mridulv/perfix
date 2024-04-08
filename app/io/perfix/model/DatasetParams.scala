package io.perfix.model

import play.api.libs.json.{Format, Json}

class DatasetParams(rows: Int, columns: Seq[ColumnDescription])

object DatasetParams {
  implicit val DatasetParams: Format[DatasetParams] = Json.format[DatasetParams]
}

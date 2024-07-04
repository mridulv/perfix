package io.perfix.model.api

import io.perfix.model.ColumnDescription
import play.api.libs.json.{Format, Json}

case class DatasetTableParams(tableName: Option[String] = None,
                              numRows: Int,
                              columns: Seq[ColumnDescription])

object DatasetTableParams {
  implicit val DatasetTableParamsFormatter: Format[DatasetTableParams] = Json.format[DatasetTableParams]
}

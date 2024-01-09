package io.perfix.model

import play.api.libs.json.{Json, Reads, Writes}

case class ColumnDescription(columnName: String, columnType: ColumnType)

object ColumnDescription {
  implicit val ColumnDescriptionWrites: Writes[ColumnDescription] = Json.writes[ColumnDescription]
  implicit val ColumnDescriptionReads: Reads[ColumnDescription] = Json.reads[ColumnDescription]
}


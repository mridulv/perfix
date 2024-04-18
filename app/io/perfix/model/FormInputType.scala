package io.perfix.model

import play.api.libs.json.{Json, Reads, Writes}

case class FormInputType(dataType: DataType, isRequired: Boolean = true)

object FormInputType {
  implicit val reads: Reads[FormInputType] = Json.reads[FormInputType]
  implicit val writes: Writes[FormInputType] = Json.writes[FormInputType]
}



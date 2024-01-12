package io.perfix.model

import play.api.libs.json.{Json, Reads, Writes}

case class QuestionType(dataType: DataType, isRequired: Boolean = true)

object QuestionType {
  implicit val reads: Reads[QuestionType] = Json.reads[QuestionType]
  implicit val writes: Writes[QuestionType] = Json.writes[QuestionType]
}



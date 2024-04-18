package io.perfix.model

import io.perfix.question.Form.QuestionLabel
import play.api.libs.json._

case class PerfixQuestion(questions: Map[QuestionLabel, QuestionType])

object PerfixQuestion {
  implicit val reads: Reads[PerfixQuestion] = Json.reads[PerfixQuestion]
  implicit val writes: Writes[PerfixQuestion] = Json.writes[PerfixQuestion]
}

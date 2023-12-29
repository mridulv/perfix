package io.perfix.model

import io.perfix.question.Question.QuestionLabel
import play.api.libs.json.{Json, Reads, Writes}

case class PerfixQuestion(questions: Map[QuestionLabel, DataType])

object PerfixQuestion {
  implicit val perfixQuestionWrites: Writes[PerfixQuestion] = Json.writes[PerfixQuestion]
  implicit val perfixQuestionReads: Reads[PerfixQuestion] = Json.reads[PerfixQuestion]
}

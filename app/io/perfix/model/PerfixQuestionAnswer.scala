package io.perfix.model

import io.perfix.question.Question.QuestionLabel
import play.api.libs.json.{Json, Reads, Writes}

case class PerfixQuestionAnswer(questionLabel: QuestionLabel, answer: Any)

object PerfixQuestionAnswer {
  implicit val PerfixQuestionAnswerWrites: Writes[PerfixQuestionAnswer] = Json.writes[PerfixQuestionAnswer]
  implicit val PerfixQuestionAnswerReads: Reads[PerfixQuestionAnswer] = Json.reads[PerfixQuestionAnswer]
}

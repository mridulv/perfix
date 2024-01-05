package io.perfix.model

import io.perfix.question.Question.QuestionLabel
import play.api.libs.json.{Json, Reads, Writes}

case class PerfixQuestionAnswers(answers: Seq[PerfixQuestionAnswer]) {

  def toMap: Map[QuestionLabel, Any] = {
    answers.map { ans =>
      ans.questionLabel -> ans.answer
    }.toMap
  }

}

object PerfixQuestionAnswers {
  implicit val PerfixQuestionAnswersWrites: Writes[PerfixQuestionAnswers] = Json.writes[PerfixQuestionAnswers]
  implicit val PerfixQuestionAnswersReads: Reads[PerfixQuestionAnswers] = Json.reads[PerfixQuestionAnswers]
}

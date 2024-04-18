package io.perfix.model

import io.perfix.question.Form.QuestionLabel
import play.api.libs.json.{Json, Reads, Writes}

case class PerfixQuestionAnswers(answers: Seq[PerfixQuestionAnswer]) {

  def toMap: Map[QuestionLabel, Any] = {
    answers.map { ans =>
      ans.questionLabel -> ans.answer
    }.toMap
  }

  def addPerfixQuestionAnswer(perfixQuestionAnswer: PerfixQuestionAnswer): PerfixQuestionAnswers = {
    this.copy(answers ++ Seq(perfixQuestionAnswer))
  }

  def addPerfixQuestionAnswers(perfixQuestionAnswers: Seq[PerfixQuestionAnswer]): PerfixQuestionAnswers = {
    this.copy(answers ++ perfixQuestionAnswers)
  }

}

object PerfixQuestionAnswers {
  implicit val PerfixQuestionAnswersWrites: Writes[PerfixQuestionAnswers] = Json.writes[PerfixQuestionAnswers]
  implicit val PerfixQuestionAnswersReads: Reads[PerfixQuestionAnswers] = Json.reads[PerfixQuestionAnswers]

  def empty: PerfixQuestionAnswers = {
    PerfixQuestionAnswers(Seq.empty)
  }
}

package io.perfix.model

import play.api.libs.json.{Json, Reads, Writes}

case class PerfixQuestionAnswers(answers: Seq[PerfixQuestionAnswer])

object PerfixQuestionAnswers {
  implicit val PerfixQuestionAnswersWrites: Writes[PerfixQuestionAnswers] = Json.writes[PerfixQuestionAnswers]
  implicit val PerfixQuestionAnswersReads: Reads[PerfixQuestionAnswers] = Json.reads[PerfixQuestionAnswers]
}

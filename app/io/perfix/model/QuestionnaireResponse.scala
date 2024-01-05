package io.perfix.model

import play.api.libs.json.{Json, Reads, Writes}

case class QuestionnaireResponse(id: Int, numQuestions: Int)

object QuestionnaireResponse {
  implicit val perfixQuestionnaireWrites: Writes[QuestionnaireResponse] = Json.writes[QuestionnaireResponse]
  implicit val perfixQuestionnaireReads: Reads[QuestionnaireResponse] = Json.reads[QuestionnaireResponse]
}

package io.perfix.model

import play.api.libs.json.{Json, Reads, Writes}

case class PerfixQuestionnaire(id: Int, numQuestions: Int)

object PerfixQuestionnaire {
  implicit val perfixQuestionnaireWrites: Writes[PerfixQuestionnaire] = Json.writes[PerfixQuestionnaire]
  implicit val perfixQuestionnaireReads: Reads[PerfixQuestionnaire] = Json.reads[PerfixQuestionnaire]
}

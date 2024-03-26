package io.perfix.model

import play.api.libs.json.{Format, Json}
import PerfixQuestionAnswer._
case class ValueProbability(value: Any, probability: Int)

object ValueProbability {
  implicit val ValueProbability: Format[ValueProbability] = Json.format[ValueProbability]
}


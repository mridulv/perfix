package io.perfix.model

import play.api.libs.json.{Format, Json}
import PerfixQuestionAnswer._
import PerfixQuestionAnswer._
case class ValueProbability(value: Any, probability: Double)

object ValueProbability {
  implicit val ValueProbability: Format[ValueProbability] = Json.format[ValueProbability]
}


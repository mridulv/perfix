package io.perfix.model

import play.api.libs.json.{Format, Json}
import PerfixQuestionAnswer._
case class ValueProbability(value: Any, probability: Int) {

  def isValid: Boolean = {
    probability <= 100 && probability >= 0
  }

}

object ValueProbability {
  implicit val ValueProbability: Format[ValueProbability] = Json.format[ValueProbability]
}


package io.perfix.model

import play.api.libs.json.{Format, Json}

case class ValueProbabilities(valueProbabilities: Seq[ValueProbability]) {
  def generateValue(defaultValue: Any, randomGen: () => Int): Any = {
    val rnd = randomGen()
    var accumulatedProbability = 0.0

    valueProbabilities.foreach { vp =>
      accumulatedProbability += vp.probability
      if (rnd <= accumulatedProbability) return vp.value
    }

    defaultValue
  }

  def isValid: Boolean = {
    valueProbabilities.forall(_.isValid) && valueProbabilities.map(_.probability).sum >= 0 && valueProbabilities.map(_.probability).sum <= 100
  }

}

object ValueProbabilities {
  implicit val ValueProbabilities: Format[ValueProbabilities] = Json.format[ValueProbabilities]
}

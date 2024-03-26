package io.perfix.model

import play.api.libs.json.{Format, Json}

case class ValueProbabilities(valueProbabilities: Seq[ValueProbability]) {
  import scala.util.Random
  def generateValue(defaultValue: Any, randomGen: () => Int): Any = {
    val rnd = randomGen()
    var accumulatedProbability = 0.0

    valueProbabilities.foreach { vp =>
      accumulatedProbability += vp.probability
      if (rnd <= accumulatedProbability) return vp.value
    }

    defaultValue
  }

}

object ValueProbabilities {
  implicit val ValueProbabilities: Format[ValueProbabilities] = Json.format[ValueProbabilities]
}

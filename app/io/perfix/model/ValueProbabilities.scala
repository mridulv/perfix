package io.perfix.model

import play.api.libs.json.{Format, Json}

case class ValueProbabilities(valueProbabilities: Seq[ValueProbability]) {
  import scala.util.Random
  def generateValue(randomGen: () => Any): Any = {
    val rnd = Random.nextDouble() * 10000
    var accumulatedProbability = 0.0

    valueProbabilities.foreach { vp =>
      accumulatedProbability += vp.probability
      if (rnd <= accumulatedProbability * 10000) return vp.value
    }

    randomGen()
  }

}

object ValueProbabilities {
  implicit val ValueProbabilities: Format[ValueProbabilities] = Json.format[ValueProbabilities]
}

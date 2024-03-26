package io.perfix.model

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import scala.util.Random

class ValueProbabilitiesTest extends AnyFlatSpec with Matchers  {

  "ValueProbabilities" should "generateValue returns correct value based on random number" in {
    val vpSeq = Seq(
      ValueProbability("value_1", 30),
      ValueProbability("value_2", 30),
      ValueProbability("value_3", 40)
    )
    val valueProbabilities = ValueProbabilities(vpSeq)

    val res = Seq(15, 45, 75).zip(vpSeq).map { case (mockValue, valueProbability) =>
      valueProbabilities.generateValue("Random value", () => mockValue)
    }

    res should be(Seq("value_1", "value_2", "value_3"))
  }

  "ValueProbabilities" should "generateValue returns 'Random Value' when random number is beyond defined probabilities" in {
    val vpSeq = Seq(
      ValueProbability("value_1", 10),
      ValueProbability("value_2", 80)
    )
    val valueProbabilities = ValueProbabilities(vpSeq)
    val result = valueProbabilities.generateValue("Random value", () => 99)
    vpSeq.map(_.value).contains(result) should be(false)
  }
}

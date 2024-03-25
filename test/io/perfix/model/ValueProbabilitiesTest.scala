package io.perfix.model

import org.scalatest.funsuite.AnyFunSuite

import scala.util.Random

class ValueProbabilitiesTest extends AnyFunSuite {

  // Mock Random to control the output of random numbers for predictable testing
  object MockRandom extends Random {
    var nextDoubleValue: Double = 0.0
    override def nextDouble(): Double = nextDoubleValue
  }

  test("generateValue returns correct value based on random number") {
    // Setup
    val vpSeq = Seq(
      ValueProbability("value_1", 30.0),
      ValueProbability("value_2", 30.0),
      ValueProbability("value_3", 40.0) // Adjusted to ensure total is 100
    )
    val valueProbabilities = ValueProbabilities(vpSeq)

    // This block simulates the scenario for each value based on the random number
    Seq(0.15, 0.45, 0.75).zip(vpSeq).foreach { case (mockValue, vp) =>
      MockRandom.nextDoubleValue = mockValue * 100 // Adjust the mock random value
      val result = valueProbabilities.generateValue()(MockRandom) // Pass the mock Random instance
      assert(result == vp.value) // Check if the generated value matches the expected value
    }
  }

  test("generateValue returns 'Random Value' when random number is beyond defined probabilities") {
    val vpSeq = Seq(
      ValueProbability("value_1", 10.0),
      ValueProbability("value_2", 20.0)
    )
    val valueProbabilities = ValueProbabilities(vpSeq)
    MockRandom.nextDoubleValue = 99 // Simulate a high random value outside the defined probabilities

    // Act
    val result = valueProbabilities.generateValue()(MockRandom) // Pass the mock Random instance

    // Assert
    assert(result == "Random Value") // Check that the fallback "Random Value" is returned
  }
}

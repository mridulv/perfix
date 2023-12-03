package io.perfix.generator

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import io.perfix.model.{ColumnDescription, DataDescription, NameType, NumericRangeConstraint, NumericType}

class FakeDataGeneratorTest extends AnyFlatSpec with Matchers {

  "FakeDataGenerator" should "generate data according to the provided DataDescription" in {
    val columns = Seq(
      ColumnDescription("name", NameType),
      ColumnDescription("age", NumericType(Some(NumericRangeConstraint(18, 30))))
      // Add more columns as needed
    )
    val dataDescription = DataDescription(10, columns)
    val generator = new FakeDataGenerator()
    val data = generator.generateData(dataDescription)

    data should have length 10
    data.foreach { row =>
      row should contain key "name"
      row("name") shouldBe a [String]

      row should contain key "age"
      row("age") shouldBe a [Integer]
      row("age").asInstanceOf[Int] should (be >= 18 and be <= 30)
    }
  }
}

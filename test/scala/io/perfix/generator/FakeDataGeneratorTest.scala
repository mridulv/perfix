package io.perfix.generator

import io.perfix.model.{ColumnDescription, DataDescription, NameType, NumericRangeConstraint, NumericType}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class FakeDataGeneratorTest extends AnyFlatSpec with Matchers {

  "FakeDataGenerator" should "generate data according to the provided DataDescription" in {
    val columns = Seq(
      ColumnDescription("name", NameType),
      ColumnDescription("age", NumericType(Some(NumericRangeConstraint(18, 30))))
      // Add more columns as needed
    )
    val dataDescription = DataDescription()
    dataDescription.columns = columns
    dataDescription.rows = 10
    val generator = new FakeDataGenerator()
    val data = generator.generateData(dataDescription)

    data.data.length should be(10)
    data.data.foreach { row =>
      row should contain key "name"
      row("name") shouldBe a [String]

      row should contain key "age"
      row("age") shouldBe a [Integer]
      row("age").asInstanceOf[Int] should (be >= 18 and be <= 30)
    }
  }
}

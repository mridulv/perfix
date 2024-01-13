package io.perfix.generator

import io.perfix.model._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class FakeDataGeneratorTest extends AnyFlatSpec with Matchers {
  "FakeDataGenerator" should "generate fake data with specified number of rows and columns" in {
    val dataDescription = DataDescription()
    dataDescription.rowsOpt = Some(5)
    dataDescription.columnsOpt = Some(Seq(ColumnDescription("name", NameType(isUnique = true)), ColumnDescription("age", NumericType(None))))

    val fakeDataGenerator = new FakeDataGenerator
    val dataWithDescription = fakeDataGenerator.generateData(dataDescription)

    dataWithDescription.dataDescription.rows shouldEqual 5
    dataWithDescription.dataDescription.columns should have size 2
    dataWithDescription.data should have size 5
  }

  it should "generate unique values for columns marked as unique" in {
    val dataDescription = DataDescription()
    dataDescription.rowsOpt = Some(10)
    dataDescription.columnsOpt = Some(Seq(ColumnDescription("id", TextType(isUnique = true))))

    val fakeDataGenerator = new FakeDataGenerator
    val dataWithDescription = fakeDataGenerator.generateData(dataDescription)

    val ids = dataWithDescription.data.map(_("id")).distinct
    ids should have size 10
  }

  it should "generate data with default values for unsupported data types" in {
    val dataDescription = DataDescription()
    dataDescription.rowsOpt = Some(5)
    dataDescription.columnsOpt = Some(Seq(ColumnDescription("name", NameType(isUnique = true)), ColumnDescription("score", NumericType(None))))

    val fakeDataGenerator = new FakeDataGenerator
    val dataWithDescription = fakeDataGenerator.generateData(dataDescription)

    dataWithDescription.data.foreach { row =>
      row("score") shouldEqual "default_value" // Replace 'default_value' with the actual default value
    }
  }
}
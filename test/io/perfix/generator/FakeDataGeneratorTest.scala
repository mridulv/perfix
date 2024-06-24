package io.perfix.generator

import io.perfix.model._
import io.perfix.model.api.DatasetParams
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.util.Random

class FakeDataGeneratorTest extends AnyFlatSpec with Matchers {
  "FakeDataGenerator" should "generate fake data with specified number of rows and columns" in {
    val datasetParams = DatasetParams(
      None,
      name = s"dataset-${Random.nextInt()}",
      description = "desc",
      5,
      Seq(ColumnDescription("name", NameType(isUnique = true)), ColumnDescription("age", NumericType(None)))
    )

    val fakeDataGenerator = new FakeDataGenerator
    val dataset = fakeDataGenerator.generateData(datasetParams)

    dataset.params.rows shouldEqual 5
    dataset.params.columns should have size 2
    dataset.data should have size 5
  }

  it should "generate unique values for columns marked as unique" in {
    val datasetParams = DatasetParams(
      None,
      name = s"dataset-${Random.nextInt()}",
      description = "desc",
      10,
      Seq(ColumnDescription("id", TextType(isUnique = true)))
    )

    val fakeDataGenerator = new FakeDataGenerator
    val dataset = fakeDataGenerator.generateData(datasetParams)

    val ids = dataset.data.map(_("id")).distinct
    ids should have size 10
  }

  it should "generate data with default values for unsupported data types" in {
    val datasetParams = DatasetParams(
      None,
      name = s"dataset-${Random.nextInt()}",
      description = "desc",
      5,
      Seq(
        ColumnDescription("name", NameType(isUnique = true)),
        ColumnDescription("score", NumericType(Some(NumericRangeConstraint(10, 11))))
      )
    )

    val fakeDataGenerator = new FakeDataGenerator
    val dataset = fakeDataGenerator.generateData(datasetParams)

    dataset.data.foreach { row =>
      row("score").toString.toInt >= 10 shouldBe true
      row("score").toString.toInt <= 11 shouldBe true
    }
  }
}
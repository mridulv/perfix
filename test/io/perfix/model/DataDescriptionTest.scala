package io.perfix.model

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class DataDescriptionTest extends AnyFlatSpec with Matchers {
  "DataDescription" should "allow setting rows and columns" in {
    val dataDescription = DataDescription()
    dataDescription.rowsOpt shouldBe None
    dataDescription.columnsOpt shouldBe None

    dataDescription.rowsOpt = Some(10)
    dataDescription.columnsOpt = Some(Seq(ColumnDescription("name", NameType()), ColumnDescription("age", NumericType(None))))

    dataDescription.rows shouldBe 10
    dataDescription.columns should have size 2
    dataDescription.setData()
    dataDescription.isDefined shouldBe true
  }

  it should "generate fake data" in {
    val dataDescription = DataDescription()
    dataDescription.rowsOpt = Some(5)
    dataDescription.columnsOpt = Some(Seq(ColumnDescription("name", NameType(isUnique = true))))

    dataDescription.fakeData shouldBe None

    dataDescription.setData()

    dataDescription.fakeData should not be None
    dataDescription.fakeData.get.data should have size 5
  }

  it should "return data if rows and columns are defined" in {
    val dataDescription = DataDescription()
    dataDescription.rowsOpt = Some(3)
    dataDescription.columnsOpt = Some(Seq(ColumnDescription("name", NameType(isUnique = true))))

    dataDescription.data shouldEqual Seq.empty

    dataDescription.setData()

    dataDescription.data should not be empty
  }
}

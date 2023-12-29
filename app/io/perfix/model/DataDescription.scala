package io.perfix.model

import io.perfix.generator.FakeDataGenerator

case class DataDescription() {

  val faker = new FakeDataGenerator
  var rowsOpt: Option[Int] = None
  var columnsOpt: Option[Seq[ColumnDescription]] = None
  var fakeData: Option[DataWithDescription] = None

  def rows: Int = {
    rowsOpt.get
  }

  def columns: Seq[ColumnDescription] = {
    columnsOpt.get
  }

  def data: Seq[Map[String, Any]] = {
    fakeData.get.data
  }

  def setData(): Unit = {
    fakeData = Some(faker.generateData(this))
  }

  def isDefined: Boolean = {
    rowsOpt.isDefined && columnsOpt.isDefined && fakeData.isDefined
  }

}

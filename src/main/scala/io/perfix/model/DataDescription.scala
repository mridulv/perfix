package io.perfix.model

case class DataDescription() {

  var rowsOpt: Option[Int] = None
  var columnsOpt: Option[Seq[ColumnDescription]] = None

  def rows: Int = {
    rowsOpt.get
  }

  def columns: Seq[ColumnDescription] = {
    columnsOpt.get
  }

  def isDefined: Boolean = {
    rowsOpt.isDefined && columnsOpt.isDefined
  }

}

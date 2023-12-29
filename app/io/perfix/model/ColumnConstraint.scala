package io.perfix.model

trait ColumnConstraint

case class NumericRangeConstraint(min: Int, max: Int) extends ColumnConstraint
case class EpochRangeConstraint(startEpoch: Long, endEpoch: Long) extends ColumnConstraint

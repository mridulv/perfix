package io.perfix.model

import com.github.javafaker.Faker

trait ColumnType {
  protected val faker: Faker = new Faker
  def getValue: Any

  def toSqlType(columnType: ColumnType): String = columnType match {
    case _: NumericType => "INT"  // or DECIMAL, depending on the precision you need
    case _: EpochType => "BIGINT" // Epoch times are typically stored as BIGINT
    case NameType => "VARCHAR(255)"
    case AddressType => "VARCHAR(255)"
    case EmailType => "VARCHAR(255)"
    case PhoneNumberType => "VARCHAR(20)"
    case URLType => "VARCHAR(255)"
    case TextType => "TEXT"        // TEXT for larger string data
    case BooleanValueType => "BOOLEAN"
  }
}

case class NumericType(constraint: Option[NumericRangeConstraint]) extends ColumnType {
  override def getValue: Any = constraint match {
    case Some(NumericRangeConstraint(min, max)) => faker.number().numberBetween(min, max)
    case _ => faker.number().randomNumber()
  }
}

case class EpochType(constraint: Option[EpochRangeConstraint]) extends ColumnType {
  override def getValue: Any = constraint match {
    case Some(EpochRangeConstraint(startEpoch, endEpoch)) => startEpoch + faker.number().numberBetween(0, endEpoch - startEpoch)
    case _ => faker.date().past(365 * 30, java.util.concurrent.TimeUnit.DAYS).getTime / 1000
  }
}

case object NameType extends ColumnType {
  override def getValue: Any = faker.name().fullName()
}

case object AddressType extends ColumnType {
  override def getValue: Any = faker.address().fullAddress()
}

case object EmailType extends ColumnType {
  override def getValue: Any = faker.internet().emailAddress()
}

case object PhoneNumberType extends ColumnType {
  override def getValue: Any = faker.phoneNumber().phoneNumber()
}

case object URLType extends ColumnType {
  override def getValue: Any = faker.internet().url()
}

case object TextType extends ColumnType {
  override def getValue: Any = faker.lorem().paragraph()
}

case object BooleanValueType extends ColumnType {
  override def getValue: Any = faker.bool().bool()
}
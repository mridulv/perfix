package io.perfix.model

import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType
import com.github.javafaker.Faker
import play.api.libs.json._

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

  def toDynamoDBType(columnType: ColumnType): String = columnType match {
    case _: NumericType => ScalarAttributeType.S.toString
    case _: EpochType => ScalarAttributeType.N.toString
    case NameType => ScalarAttributeType.S.toString
    case AddressType => ScalarAttributeType.S.toString
    case EmailType => ScalarAttributeType.S.toString
    case PhoneNumberType => ScalarAttributeType.S.toString
    case URLType => ScalarAttributeType.S.toString
    case TextType => ScalarAttributeType.S.toString
    case BooleanValueType => ScalarAttributeType.B.toString
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

object ColumnType {
  implicit val epochRangeConstraintFormat: OFormat[EpochRangeConstraint] = Json.format[EpochRangeConstraint]
  implicit val numericRangeConstantFormat: OFormat[NumericRangeConstraint] = Json.format[NumericRangeConstraint]
  implicit val numericTypeFormat: OFormat[NumericType] = Json.format[NumericType]
  implicit val epochTypeFormat: OFormat[EpochType] = Json.format[EpochType]
  implicit val nameTypeFormat: OFormat[NameType.type] = Json.format[NameType.type]
  implicit val addressTypeFormat: OFormat[AddressType.type] = Json.format[AddressType.type]
  implicit val emailTypeFormat: OFormat[EmailType.type] = Json.format[EmailType.type]
  implicit val phoneNumberTypeFormat: OFormat[PhoneNumberType.type] = Json.format[PhoneNumberType.type]
  implicit val urlTypeFormat: OFormat[URLType.type] = Json.format[URLType.type]
  implicit val textTypeFormat: OFormat[TextType.type] = Json.format[TextType.type]
  implicit val booleanValueTypeFormat: OFormat[BooleanValueType.type] = Json.format[BooleanValueType.type]

  // Define a serializer and deserializer for the ColumnType trait
  implicit val columnTypeFormat: Format[ColumnType] = Format(
    Reads {
      case json if (json \ "constraint").toOption.exists(_.isInstanceOf[JsObject]) =>
        json.validate[NumericType].orElse(json.validate[EpochType]).orElse(json.validate[NameType.type])
          .orElse(json.validate[AddressType.type]).orElse(json.validate[EmailType.type])
          .orElse(json.validate[PhoneNumberType.type]).orElse(json.validate[URLType.type])
          .orElse(json.validate[TextType.type]).orElse(json.validate[BooleanValueType.type])
      case _ =>
        JsError("Invalid ColumnType")
    },
    Writes {
      case columnType: ColumnType =>
        columnType match {
          case numericType: NumericType => numericTypeFormat.writes(numericType)
          case epochType: EpochType => epochTypeFormat.writes(epochType)
          case nameType: NameType.type => nameTypeFormat.writes(nameType)
          case addressType: AddressType.type => addressTypeFormat.writes(addressType)
          case emailType: EmailType.type => emailTypeFormat.writes(emailType)
          case phoneNumberType: PhoneNumberType.type => phoneNumberTypeFormat.writes(phoneNumberType)
          case urlType: URLType.type => urlTypeFormat.writes(urlType)
          case textType: TextType.type => textTypeFormat.writes(textType)
          case booleanValueType: BooleanValueType.type => booleanValueTypeFormat.writes(booleanValueType)
        }
    }
  )
}
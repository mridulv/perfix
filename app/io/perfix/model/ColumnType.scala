package io.perfix.model

import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType
import com.github.javafaker.Faker
import play.api.libs.json._

trait ColumnType {
  protected val faker: Faker = new Faker
  def getValue: Any
  def isUnique: Boolean
}

case class NumericType(constraint: Option[NumericRangeConstraint],
                       override val isUnique: Boolean = false) extends ColumnType {
  override def getValue: Any = constraint match {
    case Some(NumericRangeConstraint(min, max)) => faker.number().numberBetween(min, max)
    case _ => faker.number().randomNumber()
  }
}

case class EpochType(constraint: Option[EpochRangeConstraint],
                     override val isUnique: Boolean = false) extends ColumnType {
  override def getValue: Any = constraint match {
    case Some(EpochRangeConstraint(startEpoch, endEpoch)) => startEpoch + faker.number().numberBetween(0, endEpoch - startEpoch)
    case _ => faker.date().past(365 * 30, java.util.concurrent.TimeUnit.DAYS).getTime / 1000
  }
}

case class NameType(override val isUnique: Boolean = false) extends ColumnType {
  override def getValue: Any = faker.name().fullName()
}

case class AddressType(override val isUnique: Boolean = false) extends ColumnType {
  override def getValue: Any = faker.address().fullAddress()
}

case class EmailType(override val isUnique: Boolean = false) extends ColumnType {
  override def getValue: Any = faker.internet().emailAddress()
}

case class PhoneNumberType(override val isUnique: Boolean = false) extends ColumnType {
  override def getValue: Any = faker.phoneNumber().phoneNumber()
}

case class URLType(override val isUnique: Boolean = false) extends ColumnType {
  override def getValue: Any = faker.internet().url()
}

case class TextType(override val isUnique: Boolean = false) extends ColumnType {
  override def getValue: Any = faker.lorem().paragraph()
}

case class BooleanValueType(override val isUnique: Boolean = false) extends ColumnType {
  override def getValue: Any = faker.bool().bool()
}

object ColumnType {
  implicit val epochRangeConstraintFormat: OFormat[EpochRangeConstraint] = Json.format[EpochRangeConstraint]
  implicit val numericRangeConstantFormat: OFormat[NumericRangeConstraint] = Json.format[NumericRangeConstraint]
  implicit val numericTypeFormat: Format[NumericType] = Json.format[NumericType]
  implicit val epochTypeFormat: Format[EpochType] = Json.format[EpochType]
  implicit val nameTypeFormat: Format[NameType] = Json.format[NameType]
  implicit val addressTypeFormat: Format[AddressType] = Json.format[AddressType]
  implicit val emailTypeFormat: Format[EmailType] = Json.format[EmailType]
  implicit val phoneNumberTypeFormat: Format[PhoneNumberType] = Json.format[PhoneNumberType]
  implicit val urlTypeFormat: Format[URLType] = Json.format[URLType]
  implicit val textTypeFormat: Format[TextType] = Json.format[TextType]
  implicit val booleanValueTypeFormat: Format[BooleanValueType] = Json.format[BooleanValueType]

  // Define a custom JSON format for the ColumnType trait
  implicit val columnTypeFormat: Format[ColumnType] = new Format[ColumnType] {
    override def writes(o: ColumnType): JsValue = {
      val (typeField, jsonValue) = o match {
        case numericType: NumericType => ("NumericType", Json.toJson(numericType))
        case epochType: EpochType => ("EpochType", Json.toJson(epochType))
        case nameType: NameType => ("NameType", Json.toJson(nameType))
        case addressType: AddressType => ("AddressType", Json.toJson(addressType))
        case emailType: EmailType => ("EmailType", Json.toJson(emailType))
        case phoneNumberType: PhoneNumberType => ("PhoneNumberType", Json.toJson(phoneNumberType))
        case urlType: URLType => ("URLType", Json.toJson(urlType))
        case textType: TextType => ("TextType", Json.toJson(textType))
        case booleanValueType: BooleanValueType => ("BooleanValueType", Json.toJson(booleanValueType))
      }
      Json.obj("type" -> typeField) ++ jsonValue.asInstanceOf[JsObject]
    }

    override def reads(json: JsValue): JsResult[ColumnType] = {
      (json \ "type").validate[String] flatMap {
        case "NumericType" => json.validate[NumericType]
        case "EpochType" => json.validate[EpochType]
        case "NameType" => json.validate[NameType]
        case "AddressType" => json.validate[AddressType]
        case "EmailType" => json.validate[EmailType]
        case "PhoneNumberType" => json.validate[PhoneNumberType]
        case "URLType" => json.validate[URLType]
        case "TextType" => json.validate[TextType]
        case "BooleanValueType" => json.validate[BooleanValueType]
        case otherType => JsError(s"Unknown ColumnType subtype: $otherType")
      }
    }
  }

  def toSqlType(columnType: ColumnType): String = columnType match {
    case _: NumericType => "INT"  // or DECIMAL, depending on the precision you need
    case _: EpochType => "BIGINT" // Epoch times are typically stored as BIGINT
    case _: NameType => "VARCHAR(255)"
    case _: AddressType => "VARCHAR(255)"
    case _: EmailType => "VARCHAR(255)"
    case _: PhoneNumberType => "VARCHAR(20)"
    case _: URLType => "VARCHAR(255)"
    case _: TextType => "TEXT"        // TEXT for larger string data
    case _: BooleanValueType => "BOOLEAN"
  }

  def toDynamoDBType(columnType: ColumnType): String = columnType match {
    case _: NumericType => ScalarAttributeType.S.toString
    case _: EpochType => ScalarAttributeType.N.toString
    case _: NameType => ScalarAttributeType.S.toString
    case _: AddressType => ScalarAttributeType.S.toString
    case _: EmailType => ScalarAttributeType.S.toString
    case _: PhoneNumberType => ScalarAttributeType.S.toString
    case _: URLType => ScalarAttributeType.S.toString
    case _: TextType => ScalarAttributeType.S.toString
    case _: BooleanValueType => ScalarAttributeType.B.toString
  }
}
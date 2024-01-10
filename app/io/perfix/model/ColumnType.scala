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
  implicit val numericTypeFormat: Format[NumericType] = Json.format[NumericType]
  implicit val epochTypeFormat: Format[EpochType] = Json.format[EpochType]
  implicit val nameTypeFormat: Format[NameType.type] = Format(Reads(_ => JsSuccess(NameType)), Writes(_ => JsNull))
  implicit val addressTypeFormat: Format[AddressType.type] = Format(Reads(_ => JsSuccess(AddressType)), Writes(_ => JsNull))
  implicit val emailTypeFormat: Format[EmailType.type] = Format(Reads(_ => JsSuccess(EmailType)), Writes(_ => JsNull))
  implicit val phoneNumberTypeFormat: Format[PhoneNumberType.type] = Format(Reads(_ => JsSuccess(PhoneNumberType)), Writes(_ => JsNull))
  implicit val urlTypeFormat: Format[URLType.type] = Format(Reads(_ => JsSuccess(URLType)), Writes(_ => JsNull))
  implicit val textTypeFormat: Format[TextType.type] = Format(Reads(_ => JsSuccess(TextType)), Writes(_ => JsNull))
  implicit val booleanValueTypeFormat: Format[BooleanValueType.type] = Format(Reads(_ => JsSuccess(BooleanValueType)), Writes(_ => JsNull))

  implicit val columnTypeFormat: Format[ColumnType] = Format(
    Reads { json =>
      (json \ "type").as[String] match {
        case "NumericType" => Json.fromJson[NumericType](json)
        case "EpochType" => Json.fromJson[EpochType](json)
        case "NameType" => JsSuccess(NameType)
        case "AddressType" => JsSuccess(AddressType)
        case "EmailType" => JsSuccess(EmailType)
        case "PhoneNumberType" => JsSuccess(PhoneNumberType)
        case "URLType" => JsSuccess(URLType)
        case "TextType" => JsSuccess(TextType)
        case "BooleanValueType" => JsSuccess(BooleanValueType)
        case _ => JsError("Unknown ColumnType")
      }
    },
    Writes {
      case numericType: NumericType => Json.toJson(numericType)(numericTypeFormat).as[JsObject] + ("type" -> JsString("NumericType"))
      case epochType: EpochType => Json.toJson(epochType)(epochTypeFormat).as[JsObject] + ("type" -> JsString("EpochType"))
      case NameType => Json.obj("type" -> "NameType")
      case AddressType => Json.obj("type" -> "AddressType")
      case EmailType => Json.obj("type" -> "EmailType")
      case PhoneNumberType => Json.obj("type" -> "PhoneNumberType")
      case URLType => Json.obj("type" -> "URLType")
      case TextType => Json.obj("type" -> "TextType")
      case BooleanValueType => Json.obj("type" -> "BooleanValueType")
    }
  )
}
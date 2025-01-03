package io.perfix.model.api

import io.perfix.model.DataType
import play.api.libs.json._

case class FormInputType(dataType: DataType, isRequired: Boolean = true, defaultValue: Option[Any] = None)

object FormInputType {
  implicit val anyReads: Reads[Any] = Reads {
    case JsString(str) => JsSuccess(str)
    case JsNumber(num) if num.isValidInt => JsSuccess(num.toInt)
    case JsBoolean(b) => JsSuccess(b)
    case _ => JsError("Invalid Any type")
  }

  // Custom Writes for serializing Any
  implicit val anyWrites: Writes[Any] = Writes {
    case str: String => JsString(str)
    case int: Int => JsNumber(int)
    case b: Boolean => JsBoolean(b)
    case _ => throw new UnsupportedOperationException("Serialization of this Any type is not supported")
  }

  implicit val reads: Reads[FormInputType] = Json.reads[FormInputType]
  implicit val writes: Writes[FormInputType] = Json.writes[FormInputType]
}



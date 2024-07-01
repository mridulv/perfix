package io.perfix.model

import play.api.libs.json._

case class ValueProbability(value: Any, probability: Int) {

  def isValid: Boolean = {
    probability <= 100 && probability >= 0
  }

}

object ValueProbability {
  implicit val ValueProbability: Format[ValueProbability] = Json.format[ValueProbability]

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
}


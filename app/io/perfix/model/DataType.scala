package io.perfix.model

import play.api.libs.json._

trait DataType

case object BooleanType extends DataType
case object StringType extends DataType
case object DoubleType extends DataType
case object IntType extends DataType
case object GSIType extends DataType
case object SingleColumnSelectorType extends DataType
case object MultiColumnSelectorType extends DataType


object DataType {
  // Define a custom Reads for deserialization
  implicit val dataTypeReads: Reads[DataType] = Reads {
    case JsString("BooleanType") => JsSuccess(BooleanType)
    case JsString("StringType") => JsSuccess(StringType)
    case JsString("DoubleType") => JsSuccess(DoubleType)
    case JsString("IntType") => JsSuccess(IntType)
    case JsString("GSIType") => JsSuccess(GSIType)
    case JsString("SingleColumnSelectorType") => JsSuccess(SingleColumnSelectorType)
    case JsString("MultiColumnSelectorType") => JsSuccess(MultiColumnSelectorType)
    case _ => JsError("Invalid DataType")
  }

  // Define a custom Writes for serialization
  implicit val dataTypeWrites: Writes[DataType] = Writes {
    case BooleanType => JsString("BooleanType")
    case StringType => JsString("StringType")
    case DoubleType => JsString("DoubleType")
    case IntType => JsString("IntType")
    case GSIType => JsString("GSIType")
    case SingleColumnSelectorType => JsString("SingleColumnSelectorType")
    case MultiColumnSelectorType => JsString("MultiColumnSelectorType")
  }

  // Define a Format that combines Reads and Writes
  implicit val dataTypeFormat: Format[DataType] = Format(dataTypeReads, dataTypeWrites)
}

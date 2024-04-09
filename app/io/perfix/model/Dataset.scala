package io.perfix.model

import play.api.libs.json.{Format, JsBoolean, JsError, JsNumber, JsString, JsSuccess, Json, Reads, Writes}

case class Dataset(params: DatasetParams, data: Seq[Map[String, Any]]) {

  def sampleDataset(numRows: Int): Dataset = {
    Dataset(params, data.take(Math.max(numRows, data.size)))
  }

}

object Dataset {
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
  implicit val DatasetFormatter: Format[Dataset] = Json.format[Dataset]
}



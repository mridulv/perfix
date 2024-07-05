package io.perfix.model.api

import io.perfix.model.ColumnDescription
import play.api.libs.json._

case class Dataset(tableName: Option[String],
                   columns: Seq[ColumnDescription],
                   data: Seq[Map[String, Any]]) {

  def sampleDataset(numRows: Int): Dataset = {
    Dataset(tableName, columns, data.take(Math.min(numRows, data.size)))
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
    case long: Long => JsNumber(long)
    case b: Boolean => JsBoolean(b)
    case c => throw new UnsupportedOperationException(s"Serialization of this Any ${c} type is not supported")
  }

  implicit val reads: Reads[Dataset] = Json.reads[Dataset]
  implicit val writes: Writes[Dataset] = Json.writes[Dataset]

  def datasetForTesting: Dataset = {
    DatasetParams.empty().datasets.datasets.head
  }
}



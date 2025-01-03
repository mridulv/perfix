package io.perfix.model.api

import play.api.libs.json.{Format, Json}

case class DatasetId(id: Int)

object DatasetId {
  implicit val DatasetId: Format[DatasetId] = Json.format[DatasetId]
}

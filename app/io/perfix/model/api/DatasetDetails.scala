package io.perfix.model.api

import play.api.libs.json.{Format, Json}

case class DatasetDetails(datasetId: DatasetId, datasetName: Option[String] = None)

object DatasetDetails {
  implicit val DatasetDetailsFormatter: Format[DatasetDetails] = Json.format[DatasetDetails]
}

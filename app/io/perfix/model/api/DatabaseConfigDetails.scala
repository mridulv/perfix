package io.perfix.model.api

import play.api.libs.json.{Format, Json}

case class DatabaseConfigDetails(databaseConfigId: DatabaseConfigId,
                                 databaseConfigName: Option[String] = None,
                                 storeType: Option[String] = None,
                                 datasetName: Option[String] = None)

object DatabaseConfigDetails {
  implicit val DatabaseConfigDetailsFormatter: Format[DatabaseConfigDetails] = Json.format[DatabaseConfigDetails]
}

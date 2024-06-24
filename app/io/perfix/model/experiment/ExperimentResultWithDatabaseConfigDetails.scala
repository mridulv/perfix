package io.perfix.model.experiment

import io.perfix.model.api.DatabaseConfigDetails
import play.api.libs.json.{Format, Json}

case class ExperimentResultWithDatabaseConfigDetails(databaseConfigDetails: DatabaseConfigDetails,
                                                     experimentResult: SingleExperimentResult)

object ExperimentResultWithDatabaseConfigDetails {
  implicit val ExperimentResultWithDatabaseConfigDetailsFormatter: Format[ExperimentResultWithDatabaseConfigDetails] = Json.format[ExperimentResultWithDatabaseConfigDetails]
}
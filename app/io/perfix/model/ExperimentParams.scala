package io.perfix.model

import play.api.libs.json.{Format, Json}

case class ExperimentParams(experimentId: ExperimentId,
                            datasetId: DatasetId,
                            databaseConfigId: DatabaseConfigId,
                            experimentResult: Option[ExperimentResult] = None)

object ExperimentParams {
  implicit val ExperimentParamsFormatter: Format[ExperimentParams] = Json.format[ExperimentParams]
}

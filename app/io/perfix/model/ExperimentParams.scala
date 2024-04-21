package io.perfix.model

import io.perfix.query.PerfixQuery
import play.api.libs.json.{Format, Json}

case class ExperimentParams(experimentId: Option[ExperimentId],
                            writeBatchSize: Int = 100,
                            experimentTimeInSeconds: Int = 30,
                            concurrentQueries: Int = 1,
                            query: PerfixQuery,
                            datasetId: DatasetId,
                            databaseConfigId: DatabaseConfigId,
                            experimentResult: Option[ExperimentResult] = None)

object ExperimentParams {
  implicit val ExperimentParamsFormatter: Format[ExperimentParams] = Json.format[ExperimentParams]

  def experimentParamsForTesting: ExperimentParams = {
    ExperimentParams(
      None,
      query = PerfixQuery(limitOpt = Some(100)),
      datasetId = DatasetId(-1),
      databaseConfigId = DatabaseConfigId(-1),
      experimentResult = None
    )
  }
}

package io.perfix.model.experiment

import io.perfix.model.DatabaseConfigId
import io.perfix.model.store.StoreType.StoreType
import io.perfix.query.PerfixQuery
import play.api.libs.json.{Format, Json}

case class ExperimentDisplayParams(experimentId: Option[ExperimentId],
                                   name: String,
                                   writeBatchSize: Int = 100,
                                   experimentTimeInSeconds: Int = 30,
                                   concurrentQueries: Int = 1,
                                   query: PerfixQuery,
                                   databaseConfigId: DatabaseConfigId,
                                   datasetName: String,
                                   databaseConfigName: String,
                                   experimentState: Option[ExperimentState] = None,
                                   experimentResult: Option[ExperimentResult],
                                   createdAt: Option[Long] = None) {

  def toExperimentParams: ExperimentParams = {
    ExperimentParams(
      experimentId,
      name,
      writeBatchSize,
      experimentTimeInSeconds,
      concurrentQueries,
      query,
      databaseConfigId,
      experimentState,
      experimentResult,
      createdAt
    )
  }

}

object ExperimentDisplayParams {
  implicit val ExperimentDisplayParamsFormatter: Format[ExperimentDisplayParams] = Json.format[ExperimentDisplayParams]
}

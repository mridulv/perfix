package io.perfix.model.experiment

import io.perfix.model.DatabaseConfigId
import io.perfix.model.store.StoreType.StoreType
import io.perfix.query.PerfixQuery
import io.perfix.store.tables.ExperimentRow
import play.api.libs.json.{Format, Json}

case class ExperimentParams(experimentId: Option[ExperimentId],
                            name: String,
                            writeBatchSize: Int = 100,
                            experimentTimeInSeconds: Int = 30,
                            concurrentQueries: Int = 1,
                            query: PerfixQuery,
                            databaseConfigId: DatabaseConfigId,
                            experimentState: Option[ExperimentState] = None,
                            experimentResult: Option[ExperimentResult],
                            createdAt: Option[Long] = None) {

  def toExperimentRow: ExperimentRow = {
    experimentId match {
      case Some(id) =>
        ExperimentRow(id = id.id, obj = Json.toJson(this).toString())
      case None =>
        ExperimentRow(id = -1, obj = Json.toJson(this).toString())
    }
  }

  def toExperimentDisplayParams(datasetName: String,
                                databaseConfigName: String): ExperimentDisplayParams = {
    ExperimentDisplayParams(
      experimentId = experimentId,
      name = name,
      writeBatchSize = writeBatchSize,
      experimentTimeInSeconds = experimentTimeInSeconds,
      concurrentQueries = concurrentQueries,
      query = query,
      databaseConfigId = databaseConfigId,
      datasetName = datasetName,
      databaseConfigName = databaseConfigName,
      experimentState = experimentState,
      experimentResult = experimentResult,
      createdAt = createdAt
    )
  }

}

object ExperimentParams {
  implicit val ExperimentParamsFormatter: Format[ExperimentParams] = Json.format[ExperimentParams]
}

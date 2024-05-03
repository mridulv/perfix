package io.perfix.model

import io.perfix.query.PerfixQuery
import io.perfix.store.tables.ExperimentRow
import play.api.libs.json.{Format, Json}

case class ExperimentParams(experimentId: Option[ExperimentId],
                            name: String,
                            writeBatchSize: Int = 100,
                            experimentTimeInSeconds: Int = 30,
                            concurrentQueries: Int = 1,
                            query: PerfixQuery,
                            datasetId: DatasetId,
                            databaseConfigId: DatabaseConfigId,
                            experimentResult: Option[ExperimentResult] = None) {

  def toExperimentRow: ExperimentRow = {
    experimentId match {
      case Some(id) =>
        ExperimentRow(id = id.id, obj = Json.toJson(this).toString())
      case None =>
        ExperimentRow(id = -1, obj = Json.toJson(this).toString())
    }
  }

}

object ExperimentParams {
  implicit val ExperimentParamsFormatter: Format[ExperimentParams] = Json.format[ExperimentParams]

  def experimentParamsForTesting: ExperimentParams = {
    ExperimentParams(
      None,
      name = "test-experiment",
      query = PerfixQuery(limitOpt = Some(100)),
      datasetId = DatasetId(-1),
      databaseConfigId = DatabaseConfigId(-1),
      experimentResult = None
    )
  }
}

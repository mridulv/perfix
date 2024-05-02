package io.perfix.model

import io.perfix.query.PerfixQuery
import io.perfix.store.tables.DbExperiment
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

  def toDBExperiment: DbExperiment = {
    experimentId match {
      case Some(id) =>
        DbExperiment(id = id.id, obj = Json.toJson(this).toString())
      case None =>
        DbExperiment(id = -1, obj = Json.toJson(this).toString())
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

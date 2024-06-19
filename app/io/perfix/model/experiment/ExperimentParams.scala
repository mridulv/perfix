package io.perfix.model.experiment

import io.perfix.model.api.{DatabaseConfigDetails, DatabaseConfigParams, DatasetParams}
import io.perfix.model.UserInfo
import io.perfix.query.PerfixQuery
import io.perfix.db.tables.ExperimentRow
import io.perfix.model.experiment.ExperimentState.ExperimentState
import play.api.libs.json.{Format, Json}

case class ExperimentParams(experimentId: Option[ExperimentId],
                            name: String,
                            writeBatchSize: Int = 100,
                            experimentTimeInSeconds: Int = 30,
                            concurrentQueries: Int = 1,
                            query: PerfixQuery,
                            databaseConfigs: Seq[DatabaseConfigDetails],
                            experimentState: Option[ExperimentState] = None,
                            experimentResult: Option[MultipleExperimentResult],
                            createdAt: Option[Long] = None) {

  def toExperimentRow(userInfo: UserInfo): ExperimentRow = {
    experimentId match {
      case Some(id) =>
        ExperimentRow(id = id.id, userEmail = userInfo.email, obj = Json.toJson(this).toString())
      case None =>
        ExperimentRow(id = -1, userEmail = userInfo.email, obj = Json.toJson(this).toString())
    }
  }

  def toExperimentParamsWithDatabaseDetails(allDatasets: Seq[DatasetParams],
                                            allDatabaseConfigParams: Seq[DatabaseConfigParams]): ExperimentParams = {
    val databaseConfigDetails = databaseConfigs.map { databaseConfigDetail =>
      val configId = databaseConfigDetail.databaseConfigId
      val databaseConfigParams = allDatabaseConfigParams.find(_.databaseConfigId.get == configId).headOption
      val dataset = databaseConfigParams.flatMap(params => allDatasets.find(_.id.get == params.datasetDetails.datasetId))
      (databaseConfigParams, dataset) match {
        case (Some(_), Some(_)) => DatabaseConfigDetails(
          databaseConfigParams.get.databaseConfigId.get,
          Some(databaseConfigParams.get.name),
          Some(databaseConfigParams.get.dataStore.toString),
          Some(dataset.get.name)
        )
        case (_, _) => databaseConfigDetail
      }
    }
    this.copy(databaseConfigs = databaseConfigDetails)
  }

}

object ExperimentParams {
  implicit val ExperimentParamsFormatter: Format[ExperimentParams] = Json.format[ExperimentParams]
}

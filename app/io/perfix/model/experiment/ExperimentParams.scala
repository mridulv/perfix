package io.perfix.model.experiment

import io.perfix.model.api.{DatabaseConfigDetails, DatabaseConfigId, DatabaseConfigParams, DatasetParams}
import io.perfix.model.UserInfo
import io.perfix.query.PerfixQuery
import io.perfix.db.tables.ExperimentRow
import io.perfix.model.experiment.ExperimentState.ExperimentState
import play.api.libs.json.{Format, JsResult, JsValue, Json}
import play.api.libs.json._
import play.api.libs.functional.syntax._

case class ExperimentParams(experimentId: Option[ExperimentId],
                            name: String,
                            writeBatchSize: Int = 100,
                            experimentTimeInSeconds: Int = 30,
                            concurrentQueries: Int = 1,
                            query: PerfixQuery,
                            databaseConfigs: Seq[DatabaseConfigDetails],
                            experimentState: Option[ExperimentState] = None,
                            experimentResult: Option[Map[DatabaseConfigId, SingleExperimentResult]] = None,
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
  implicit val mapFormat: Format[Map[DatabaseConfigId, SingleExperimentResult]] = new Format[Map[DatabaseConfigId, SingleExperimentResult]] {
    def reads(json: JsValue): JsResult[Map[DatabaseConfigId, SingleExperimentResult]] = {
      json.validate[Map[String, SingleExperimentResult]].map(_.map {
        case (k, v) => DatabaseConfigId(k.toInt) -> v
      })
    }

    def writes(o: Map[DatabaseConfigId, SingleExperimentResult]): JsValue = {
      Json.toJson(o.map {
        case (k, v) => k.id.toString -> v
      })
    }
  }

  implicit val ExperimentParamsFormatter: Format[ExperimentParams] = Json.format[ExperimentParams]
}

package io.perfix.model.experiment

import io.perfix.model.api.{DatabaseConfigDetails, DatabaseConfigId, DatabaseConfigParams, DatasetParams}
import io.perfix.model.UserInfo
import io.perfix.query.{DBQuery, SqlDBQueryBuilder}
import io.perfix.db.tables.ExperimentRow
import io.perfix.model.experiment.ExperimentState.ExperimentState
import io.perfix.stores.Database
import play.api.libs.json.{Format, JsResult, JsValue, Json}
import play.api.libs.json._
import play.api.libs.functional.syntax._

import scala.collection.mutable.ListBuffer

case class ExperimentParams(experimentId: Option[ExperimentId],
                            name: String,
                            writeBatchSize: Int = 100,
                            experimentTimeInSeconds: Int = 120,
                            concurrentQueries: Int = 1,
                            dbQuery: DBQuery,
                            databaseConfigs: Seq[DatabaseConfigDetails],
                            experimentState: Option[ExperimentState] = None,
                            experimentResults: Option[Seq[ExperimentResultWithDatabaseConfigDetails]] = None,
                            createdAt: Option[Long] = None) {

  def toExperimentRow(userInfo: UserInfo): ExperimentRow = {
    experimentId match {
      case Some(id) =>
        ExperimentRow(id = id.id, userEmail = userInfo.email, obj = Json.toJson(this).toString())
      case None =>
        ExperimentRow(id = -1, userEmail = userInfo.email, obj = Json.toJson(this).toString())
    }
  }

  def validate(): Seq[String] = {
    val checkForWriteBatchSize: Boolean = writeBatchSize > 10000
    val checkForConcurrentQueries: Boolean = concurrentQueries > 100
    val checkForExperimentTimeInSeconds: Boolean = experimentTimeInSeconds > 5 * 60
    val errors = ListBuffer[String]()

    if (checkForConcurrentQueries) {
      errors.append("Validation failed for concurrent queries. Expected is that concurrent queries < 100")
    }

    if (checkForWriteBatchSize) {
      errors.append("Validation failed for write batch size. Expected is that write batch size < 10000")
    }

    if (checkForExperimentTimeInSeconds) {
      errors.append("Validation failed for experiment time in seconds. Expected is that experiment time in seconds < 5 mins")
    }

    errors.toSeq
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

  def validateExperimentParams: Boolean = {
    val databases = databaseConfigs.flatMap(_.storeType)
    val categories = databases.flatMap { database =>
      Database.allDatabases.find(d => d.name.toString == database).map(e => e.databaseCategory)
    }.flatten.toSet
    categories.size == 1
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

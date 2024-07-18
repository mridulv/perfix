package io.perfix.model.api

import io.perfix.exceptions.InvalidDatabaseConfigException
import io.perfix.model.store.DatabaseSetupParams
import io.perfix.model.store.StoreType.StoreType
import io.perfix.db.tables.DatabaseConfigRow
import io.perfix.model.api.DatabaseState.DatabaseState
import play.api.libs.json.{Format, Json}

case class DatabaseConfigParams(databaseConfigId: Option[DatabaseConfigId] = None,
                                name: String,
                                databaseSetupParams: DatabaseSetupParams,
                                dataStore: StoreType,
                                createdAt: Option[Long] = None,
                                datasetDetails: DatasetDetails,
                                databaseState: DatabaseState = DatabaseState.NotStarted) {

  def toDatabaseConfigRow(userEmail: String): DatabaseConfigRow = {
    databaseConfigId match {
      case Some(id) =>
        DatabaseConfigRow(id = id.id, userEmail = userEmail, obj = Json.toJson(this).toString())
      case None =>
        DatabaseConfigRow(id = -1, userEmail = userEmail, obj = Json.toJson(this).toString())
    }
  }

  def toDatabaseConfigDisplayParams(datasetParams: Seq[DatasetParams]): DatabaseConfigParams = {
    datasetParams.find(_.id.get == datasetDetails.datasetId) match {
      case Some(dataset) => this.copy(datasetDetails = DatasetDetails(datasetDetails.datasetId, datasetName = Some(dataset.name)))
      case None => throw new InvalidDatabaseConfigException(datasetDetails.datasetId)
    }
  }

  def toDatabaseConfigDisplayParams(datasetParams: DatasetParams): DatabaseConfigParams = {
    this.copy(datasetDetails = DatasetDetails(datasetDetails.datasetId, datasetName = Some(datasetParams.name)))
  }

  def toDatabaseConfigDetails: DatabaseConfigDetails = {
    DatabaseConfigDetails(databaseConfigId = databaseConfigId.get,
      databaseConfigName = Some(name),
      storeType = Some(dataStore.toString)
    )
  }

}

object DatabaseConfigParams {
  implicit val DatabaseConfigParamsFormatter: Format[DatabaseConfigParams] = Json.format[DatabaseConfigParams]
}
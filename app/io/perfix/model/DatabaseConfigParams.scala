package io.perfix.model

import io.perfix.exceptions.InvalidDatabaseConfigException
import io.perfix.model.store.StoreParams
import io.perfix.model.store.StoreType.StoreType
import io.perfix.store.tables.DatabaseConfigRow
import play.api.libs.json.{Format, Json}

case class DatabaseConfigParams(databaseConfigId: Option[DatabaseConfigId] = None,
                                name: String,
                                storeParams: StoreParams,
                                dataStore: StoreType,
                                createdAt: Option[Long] = None,
                                datasetDetails: DatasetDetails) {

  def toDatabaseConfigRow: DatabaseConfigRow = {
    databaseConfigId match {
      case Some(id) =>
        DatabaseConfigRow(id = id.id, obj = Json.toJson(this).toString())
      case None =>
        DatabaseConfigRow(id = -1, obj = Json.toJson(this).toString())
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

}

object DatabaseConfigParams {
  implicit val DatabaseConfigParamsFormatter: Format[DatabaseConfigParams] = Json.format[DatabaseConfigParams]
}
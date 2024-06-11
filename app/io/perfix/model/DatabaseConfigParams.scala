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
                                datasetId: DatasetId) {

  def toDatabaseConfigRow: DatabaseConfigRow = {
    databaseConfigId match {
      case Some(id) =>
        DatabaseConfigRow(id = id.id, obj = Json.toJson(this).toString())
      case None =>
        DatabaseConfigRow(id = -1, obj = Json.toJson(this).toString())
    }
  }

  def toDatabaseConfigDisplayParams(datasetParams: Seq[DatasetParams]): DatabaseConfigDisplayParams = {
    datasetParams.find(_.id.get == datasetId) match {
      case Some(dataset) => DatabaseConfigDisplayParams(
        databaseConfigId = databaseConfigId,
        name = name,
        storeParams = storeParams,
        dataStore = dataStore,
        createdAt = createdAt,
        datasetName = dataset.name,
        datasetId
      )
      case None => throw new InvalidDatabaseConfigException(datasetId)
    }
  }

  def toDatabaseConfigDisplayParams(datasetParams: DatasetParams): DatabaseConfigDisplayParams = {
    DatabaseConfigDisplayParams(
      databaseConfigId = databaseConfigId,
      name = name,
      storeParams = storeParams,
      dataStore = dataStore,
      createdAt = createdAt,
      datasetName = datasetParams.name,
      datasetId
    )
  }

}

object DatabaseConfigParams {
  implicit val DatabaseConfigParamsFormatter: Format[DatabaseConfigParams] = Json.format[DatabaseConfigParams]
}
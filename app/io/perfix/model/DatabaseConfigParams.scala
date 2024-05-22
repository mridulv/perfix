package io.perfix.model

import io.perfix.model.store.{StoreParams, StoreType}
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

}

object DatabaseConfigParams {
  implicit val DatabaseConfigParamsFormatter: Format[DatabaseConfigParams] = Json.format[DatabaseConfigParams]
}
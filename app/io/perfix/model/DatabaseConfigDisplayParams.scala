package io.perfix.model

import io.perfix.model.store.StoreParams
import io.perfix.model.store.StoreType.StoreType
import play.api.libs.json.{Format, Json}

case class DatabaseConfigDisplayParams(databaseConfigId: Option[DatabaseConfigId] = None,
                                       name: String,
                                       storeParams: StoreParams,
                                       dataStore: StoreType,
                                       createdAt: Option[Long] = None,
                                       datasetName: String)

object DatabaseConfigDisplayParams {
  implicit val DatabaseConfigDisplayParamsFormatter: Format[DatabaseConfigDisplayParams] = Json.format[DatabaseConfigDisplayParams]
}

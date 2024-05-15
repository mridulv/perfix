package io.perfix.model.store

import play.api.libs.json.{Format, Json}

case class DocumentDBStoreParams(instanceClass: String = "db.t3.medium",
                                 collectionName: String,
                                 indices: Seq[String]) extends StoreParams


object DocumentDBStoreParams {
  implicit val documentDBStoreParamsFormatter: Format[DocumentDBStoreParams] = Json.format[DocumentDBStoreParams]
}

package io.perfix.stores.documentdb

import io.perfix.model.store.DatabaseConfigParams
import play.api.libs.json.{Format, Json}

case class DocumentDBDatabaseConfigParams(instanceClass: String = "db.t3.medium",
                                          collectionName: String,
                                          indices: Seq[String]) extends DatabaseConfigParams


object DocumentDBDatabaseConfigParams {
  implicit val documentDBStoreParamsFormatter: Format[DocumentDBDatabaseConfigParams] = Json.format[DocumentDBDatabaseConfigParams]
}

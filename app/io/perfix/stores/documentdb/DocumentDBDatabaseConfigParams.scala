package io.perfix.stores.documentdb

import io.perfix.model.store.DatabaseConfigParams
import play.api.libs.json.{Format, Json}

case class DocumentDBDatabaseConfigParams(instanceClass: String = "db.t3.medium",
                                          collectionName: String,
                                          indices: Seq[String]) extends DatabaseConfigParams {

  var documentDBConnectionParams: Option[DocumentDBConnectionParams] = None
  var documentDBTableParams: Option[DocumentDBTableParams] = None
  var documentDBIndicesParams: Option[DocumentDBIndicesParams] = None

}

case class DocumentDBConnectionParams(url: String, database: String)
case class DocumentDBTableParams(collectionName: String)
case class DocumentDBIndicesParams(columns: Seq[String])


object DocumentDBDatabaseConfigParams {
  implicit val documentDBStoreParamsFormatter: Format[DocumentDBDatabaseConfigParams] = Json.format[DocumentDBDatabaseConfigParams]
}

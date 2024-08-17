package io.perfix.stores.documentdb

import io.perfix.model.store.{DatabaseLaunchParams, DatabaseSetupParams}
import play.api.libs.json.{Format, Json}

case class DocumentDBDatabaseSetupParams(instanceClass: String = "db.t3.medium",
                                         collectionName: String,
                                         indices: Seq[String],
                                         dbDetails: Option[DocumentDBConnectionParams] = None) extends DatabaseSetupParams {

  override def databaseLaunchParams: DatabaseLaunchParams = DocumentDBDatabaseLaunchParams(instanceClass)
  
}

case class DocumentDBDatabaseLaunchParams(instanceClass: String = "db.t3.medium") extends DatabaseLaunchParams

case class DocumentDBConnectionParams(url: String, database: String)


object DocumentDBDatabaseSetupParams {
  implicit val DocumentDBConnectionParamsFormatter: Format[DocumentDBConnectionParams] = Json.format[DocumentDBConnectionParams]

  implicit val documentDBStoreParamsFormatter: Format[DocumentDBDatabaseSetupParams] = Json.format[DocumentDBDatabaseSetupParams]
}

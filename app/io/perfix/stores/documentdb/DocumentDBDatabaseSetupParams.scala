package io.perfix.stores.documentdb

import io.perfix.model.store.{DatabaseConnectionParams, DatabaseLaunchParams, DatabaseSetupParams}
import io.perfix.stores.mysql.MySQLConnectionParams
import io.perfix.stores.redis.RedisConnectionParams
import play.api.libs.json.{Format, Json}

case class DocumentDBDatabaseSetupParams(instanceClass: String = "db.t3.medium",
                                         collectionName: String,
                                         indices: Seq[String],
                                         dbDetails: Option[DocumentDBConnectionParams] = None) extends DatabaseSetupParams {

  override def databaseLaunchParams: DatabaseLaunchParams = DocumentDBDatabaseLaunchParams(instanceClass)

  override def update(databaseConfigDetails: Option[DatabaseConnectionParams]): DatabaseSetupParams = {
    databaseConfigDetails.map {
      case documentDBConnectionParams: DocumentDBConnectionParams => this.copy(dbDetails = Some(documentDBConnectionParams))
      case mysqlDBConnectionParams: MySQLConnectionParams => this
      case redisConnectionParams: RedisConnectionParams => this
    }.getOrElse(this)
  }
}

case class DocumentDBDatabaseLaunchParams(instanceClass: String = "db.t3.medium") extends DatabaseLaunchParams

case class DocumentDBConnectionParams(url: String, database: String) extends DatabaseConnectionParams


object DocumentDBDatabaseSetupParams {
  implicit val DocumentDBConnectionParamsFormatter: Format[DocumentDBConnectionParams] = Json.format[DocumentDBConnectionParams]

  implicit val documentDBStoreParamsFormatter: Format[DocumentDBDatabaseSetupParams] = Json.format[DocumentDBDatabaseSetupParams]
}

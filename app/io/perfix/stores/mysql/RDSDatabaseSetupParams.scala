package io.perfix.stores.mysql

import io.perfix.model.store.{DatabaseConnectionParams, DatabaseLaunchParams, DatabaseSetupParams}
import io.perfix.model.store.StoreType.{MySQL, StoreType}
import play.api.libs.json.{Format, Json}
import RDSDatabaseSetupParams._
import io.perfix.stores.documentdb.DocumentDBConnectionParams
import io.perfix.stores.redis.RedisConnectionParams

case class RDSDatabaseSetupParams(instanceType: String = "db.t3.medium",
                                  tableName: String,
                                  primaryIndexColumn: Option[String],
                                  secondaryIndexesColumn: Option[Seq[String]],
                                  dbName: Option[String] = None,
                                  dbDetails: Option[MySQLConnectionParams] = None,
                                  databaseType: Option[StoreType] = Some(MySQL)) extends DatabaseSetupParams {

  override def databaseLaunchParams: DatabaseLaunchParams = RDSDatabaseLaunchParams(instanceType)

  override def update(databaseConfigDetails: Option[DatabaseConnectionParams]): DatabaseSetupParams = {
    databaseConfigDetails.map {
      case documentDBConnectionParams: DocumentDBConnectionParams => this
      case mysqlDBConnectionParams: MySQLConnectionParams => this.copy(dbDetails = Some(mysqlDBConnectionParams))
      case redisConnectionParams: RedisConnectionParams => this
    }.getOrElse(this)
  }
}

case class RDSDatabaseLaunchParams(instanceType: String = "db.t3.medium") extends DatabaseLaunchParams

object RDSDatabaseSetupParams {
  implicit val RDSDatabaseSetupParamsFormatter: Format[RDSDatabaseSetupParams] = Json.format[RDSDatabaseSetupParams]

  def defaultRDSDatabaseSetupParams(tableName: String, storeType: StoreType): RDSDatabaseSetupParams = {
    RDSDatabaseSetupParams(
      tableName = tableName,
      databaseType = Some(storeType),
      primaryIndexColumn = None,
      secondaryIndexesColumn = None
    )
  }
}

case class MySQLConnectionParams(url: String, username: String, password: String) extends DatabaseConnectionParams

object MySQLConnectionParams {
  implicit val MySQLConnectionParamsFormatter: Format[MySQLConnectionParams] = Json.format[MySQLConnectionParams]
}

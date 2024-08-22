package io.perfix.stores.redis

import io.perfix.model.store.{DatabaseConnectionParams, DatabaseLaunchParams, DatabaseSetupParams}
import io.perfix.stores.documentdb.DocumentDBConnectionParams
import io.perfix.stores.mysql.MySQLConnectionParams
import play.api.libs.json.{Format, Json}

case class RedisLaunchParams(cacheNodeType: Option[String],
                             numCacheNodes: Option[Int]) extends DatabaseLaunchParams

case class RedisDatabaseSetupParams(cacheNodeType: Option[String],
                                    numCacheNodes: Option[Int],
                                    keyColumn: String,
                                    dbDetails: Option[RedisConnectionParams] = None) extends DatabaseSetupParams {

  override def databaseLaunchParams: DatabaseLaunchParams = RedisLaunchParams(cacheNodeType, numCacheNodes)

  override def update(databaseConfigDetails: Option[DatabaseConnectionParams]): DatabaseSetupParams = {
    databaseConfigDetails.map {
      case documentDBConnectionParams: DocumentDBConnectionParams => this
      case mysqlDBConnectionParams: MySQLConnectionParams => this
      case redisConnectionParams: RedisConnectionParams => this.copy(dbDetails = Some(redisConnectionParams))
    }.getOrElse(this)
  }
}

case class RedisConnectionParams(url: String, port: Int) extends DatabaseConnectionParams

object RedisDatabaseSetupParams {
  implicit val redisConnectionParamsFormatter: Format[RedisConnectionParams] = Json.format[RedisConnectionParams]

  implicit val RedisStoreParamsFormatter: Format[RedisDatabaseSetupParams] = Json.format[RedisDatabaseSetupParams]
}

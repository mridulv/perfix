package io.perfix.stores.redis

import io.perfix.model.store.DatabaseSetupParams
import play.api.libs.json.{Format, Json}

case class RedisDatabaseSetupParams(cacheNodeType: Option[String],
                                    numCacheNodes: Option[Int],
                                    keyColumn: String,
                                    dbDetails: Option[RedisConnectionParams] = None) extends DatabaseSetupParams

case class RedisConnectionParams(url: String, port: Int)

object RedisDatabaseSetupParams {
  implicit val redisConnectionParamsFormatter: Format[RedisConnectionParams] = Json.format[RedisConnectionParams]

  implicit val RedisStoreParamsFormatter: Format[RedisDatabaseSetupParams] = Json.format[RedisDatabaseSetupParams]
}

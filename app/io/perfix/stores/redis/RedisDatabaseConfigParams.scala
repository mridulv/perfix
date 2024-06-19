package io.perfix.stores.redis

import io.perfix.model.store.DatabaseConfigParams
import play.api.libs.json.{Format, Json}

case class RedisDatabaseConfigParams(cacheNodeType: Option[String],
                                     numCacheNodes: Option[Int],
                                     keyColumn: String) extends DatabaseConfigParams {

  var redisConnectionParams: Option[RedisConnectionParams] = None
  var redisTableParams: Option[RedisTableParams] = None

}

case class RedisConnectionParams(url: String, port: Int)
case class RedisTableParams(keyColumn: String)

object RedisDatabaseConfigParams {
  implicit val RedisStoreParamsFormatter: Format[RedisDatabaseConfigParams] = Json.format[RedisDatabaseConfigParams]
}

package io.perfix.model.store

import play.api.libs.json.{Format, Json}

case class RedisStoreParams(cacheNodeType: Option[String],
                            numCacheNodes: Option[Int],
                            keyColumn: String) extends StoreParams

object RedisStoreParams {
  implicit val RedisStoreParamsFormatter: Format[RedisStoreParams] = Json.format[RedisStoreParams]
}

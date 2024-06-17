package io.perfix.stores.redis

import io.perfix.model.store.StoreParams
import play.api.libs.json.{Format, Json}

case class RedisStoreParams(cacheNodeType: Option[String],
                            numCacheNodes: Option[Int],
                            keyColumn: String) extends StoreParams

object RedisStoreParams {
  implicit val RedisStoreParamsFormatter: Format[RedisStoreParams] = Json.format[RedisStoreParams]
}

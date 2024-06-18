package io.perfix.stores.redis

case class RedisParams() {

  var redisConnectionParams: Option[RedisConnectionParams] = None
  var redisTableParams: Option[RedisTableParams] = None

}

case class RedisConnectionParams(url: String, port: Int)
case class RedisTableParams(keyColumn: String)

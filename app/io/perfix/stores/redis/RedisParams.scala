package io.perfix.stores.redis

import io.perfix.forms.FormParams

case class RedisParams() extends FormParams {

  var redisConnectionParams: Option[RedisConnectionParams] = None
  var redisTableParams: Option[RedisTableParams] = None

}

case class RedisConnectionParams(url: String, port: Int)
case class RedisTableParams(keyColumn: String)

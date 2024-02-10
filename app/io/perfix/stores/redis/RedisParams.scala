package io.perfix.stores.redis

import io.perfix.question.QuestionParams

case class RedisParams() extends QuestionParams {

  var redisConnectionParams: Option[RedisConnectionParams] = None
  var redisTableParams: Option[RedisTableParams] = None

}

case class RedisConnectionParams(url: String, port: Int)
case class RedisTableParams(keyColumn: String)

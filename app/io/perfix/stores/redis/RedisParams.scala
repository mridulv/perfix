package io.perfix.stores.redis

import io.perfix.question.QuestionParams

case class RedisParams() extends QuestionParams {

  var url: Option[String] = None
  var port: Option[Int] = None
  var keyColumn: Option[String] = None

}

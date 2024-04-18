package io.perfix.forms.redis

import io.perfix.exceptions.ParamsAlreadyDefinedException
import io.perfix.model.{FormInputType, StringType}
import io.perfix.forms.Form
import io.perfix.forms.Form.FormInputName
import io.perfix.forms.redis.RedisTableParamsForm.KEY_COLUMN
import io.perfix.stores.redis.{RedisParams, RedisTableParams}

class RedisTableParamsForm(override val formParams: RedisParams)
  extends Form {

  override val mapping: Map[FormInputName, FormInputType] = Map(
    KEY_COLUMN -> FormInputType(StringType)
  )

  override def shouldAsk(): Boolean = {
    formParams.redisTableParams.isEmpty
  }

  override def setAnswers(answers: Map[FormInputName, Any]): Unit = {
    formParams.redisTableParams match {
      case Some(_) => throw ParamsAlreadyDefinedException("Redis Table Params are already defined")
      case None => formParams.redisTableParams = Some(RedisTableParams(answers(KEY_COLUMN).toString))
    }
  }
}

object RedisTableParamsForm {
  val KEY_COLUMN = "key_column"

  def apply(redisParams: RedisParams): RedisTableParamsForm = {
    new RedisTableParamsForm(redisParams)
  }
}
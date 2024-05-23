package io.perfix.forms.redis

import io.perfix.exceptions.ParamsAlreadyDefinedException
import io.perfix.forms.Form
import io.perfix.forms.Form.FormInputName
import io.perfix.forms.redis.RedisConnectionParametersForm.{PORT, URL}
import io.perfix.model.{FormInputType, IntType, StringType}
import io.perfix.stores.redis.{RedisConnectionParams, RedisParams}

class RedisConnectionParametersForm(override val formParams: RedisParams) extends Form {
  override val mapping: Map[FormInputName, FormInputType] = Map(
    URL -> FormInputType(StringType),
    PORT -> FormInputType(IntType)
  )

  override def shouldAsk(): Boolean = {
    formParams.redisConnectionParams.isEmpty
  }

  override def setAnswers(answers: Map[FormInputName, Any]): Unit = {
    formParams.redisConnectionParams match {
      case Some(_) => throw ParamsAlreadyDefinedException("Redis URL Already Defined")
      case None =>
        formParams.redisConnectionParams = Some(
          RedisConnectionParams(
            answers(URL).toString,
            answers(PORT).toString.toInt
          )
        )
    }
  }
}

object RedisConnectionParametersForm {
  val URL = "URL"
  val PORT = "PORT"

  def apply(redisParams: RedisParams): RedisConnectionParametersForm = {
    new RedisConnectionParametersForm(redisParams)
  }
}
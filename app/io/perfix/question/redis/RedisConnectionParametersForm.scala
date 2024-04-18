package io.perfix.question.redis

import io.perfix.exceptions.ParamsAlreadyDefinedException
import io.perfix.model.{IntType, FormInputType, StringType}
import io.perfix.question.Form
import io.perfix.question.Form.FormInputName
import io.perfix.question.redis.RedisConnectionParametersForm.{PORT, URL}
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
package io.perfix.question.redis

import io.perfix.exceptions.ParamsAlreadyDefinedException
import io.perfix.model.{QuestionType, StringType}
import io.perfix.question.Form
import io.perfix.question.Form.QuestionLabel
import io.perfix.question.redis.RedisTableParamsForm.KEY_COLUMN
import io.perfix.stores.redis.{RedisParams, RedisTableParams}

class RedisTableParamsForm(override val storeQuestionParams: RedisParams)
  extends Form {

  override val mapping: Map[QuestionLabel, QuestionType] = Map(
    KEY_COLUMN -> QuestionType(StringType)
  )

  override def shouldAsk(): Boolean = {
    storeQuestionParams.redisTableParams.isEmpty
  }

  override def setAnswers(answers: Map[QuestionLabel, Any]): Unit = {
    storeQuestionParams.redisTableParams match {
      case Some(_) => throw ParamsAlreadyDefinedException("Redis Table Params are already defined")
      case None => storeQuestionParams.redisTableParams = Some(RedisTableParams(answers(KEY_COLUMN).toString))
    }
  }
}

object RedisTableParamsForm {
  val KEY_COLUMN = "key_column"

  def apply(redisParams: RedisParams): RedisTableParamsForm = {
    new RedisTableParamsForm(redisParams)
  }
}
package io.perfix.question.redis

import io.perfix.exceptions.ParamsAlreadyDefinedException
import io.perfix.model.{DataType, QuestionType, StringType}
import io.perfix.question.Question
import io.perfix.question.Question.QuestionLabel
import io.perfix.question.redis.RedisTableParamsQuestion.KEY_COLUMN
import io.perfix.stores.redis.{RedisParams, RedisTableParams}

class RedisTableParamsQuestion(override val storeQuestionParams: RedisParams)
  extends Question {

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

object RedisTableParamsQuestion {
  val KEY_COLUMN = "key_column"

  def apply(redisParams: RedisParams): RedisTableParamsQuestion = {
    new RedisTableParamsQuestion(redisParams)
  }
}
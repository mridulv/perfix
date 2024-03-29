package io.perfix.question.redis

import io.perfix.exceptions.ParamsAlreadyDefinedException
import io.perfix.model.{IntType, QuestionType, StringType}
import io.perfix.question.Question
import io.perfix.question.Question.QuestionLabel
import io.perfix.question.redis.RedisConnectionParametersQuestion.{PORT, URL}
import io.perfix.stores.redis.{RedisConnectionParams, RedisParams}

class RedisConnectionParametersQuestion(override val storeQuestionParams: RedisParams) extends Question {
  override val mapping: Map[QuestionLabel, QuestionType] = Map(
    URL -> QuestionType(StringType),
    PORT -> QuestionType(IntType)
  )

  override def shouldAsk(): Boolean = {
    storeQuestionParams.redisConnectionParams.isEmpty
  }

  override def setAnswers(answers: Map[QuestionLabel, Any]): Unit = {
    storeQuestionParams.redisConnectionParams match {
      case Some(_) => throw ParamsAlreadyDefinedException("Redis URL Already Defined")
      case None =>
        storeQuestionParams.redisConnectionParams = Some(
          RedisConnectionParams(
            answers(URL).toString,
            answers(PORT).toString.toInt
          )
        )
    }
  }
}

object RedisConnectionParametersQuestion {
  val URL = "URL"
  val PORT = "PORT"

  def apply(redisParams: RedisParams): RedisConnectionParametersQuestion = {
    new RedisConnectionParametersQuestion(redisParams)
  }
}
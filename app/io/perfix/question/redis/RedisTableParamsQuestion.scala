package io.perfix.question.redis

import io.perfix.context.QuestionExecutionContext
import io.perfix.exceptions.ParamsAlreadyDefinedException
import io.perfix.model.{DataType, StringType}
import io.perfix.question.Question
import io.perfix.question.Question.QuestionLabel
import io.perfix.question.redis.RedisTableParamsQuestion.KEY_COLUMN
import io.perfix.stores.redis.RedisParams

class RedisTableParamsQuestion(override val storeQuestionParams: RedisParams)
  extends Question {

  override val mapping: Map[QuestionLabel, DataType] = Map(
    KEY_COLUMN -> StringType
  )

  override def shouldAsk(): Boolean = {
    storeQuestionParams.keyColumn.isEmpty
  }

  override def setAnswers(answers: Map[QuestionLabel, Any]): Unit = {
    storeQuestionParams.keyColumn match {
      case Some(_) => throw ParamsAlreadyDefinedException("Redis URL Already Defined")
      case None => storeQuestionParams.keyColumn = Some(answers(KEY_COLUMN).toString)
    }
  }
}

object RedisTableParamsQuestion {
  val KEY_COLUMN = "key_column"

  def apply(redisParams: RedisParams): RedisTableParamsQuestion = {
    new RedisTableParamsQuestion(redisParams)
  }
}
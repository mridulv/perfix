package io.perfix.question.redis

import io.perfix.context.QuestionExecutionContext
import io.perfix.exceptions.ParamsAlreadyDefinedException
import io.perfix.model.{DataType, StringType}
import io.perfix.question.Question
import io.perfix.question.Question.QuestionLabel
import io.perfix.question.redis.RedisTableParamsQuestion.KEY_COLUMN
import io.perfix.stores.redis.RedisParams

class RedisTableParamsQuestion(override val storeQuestionParams: RedisParams,
                               override val questionExecutionContext: QuestionExecutionContext)
  extends Question {

  override val mapping: Map[QuestionLabel, DataType] = Map(
    KEY_COLUMN -> StringType
  )

  override def shouldAsk(): Boolean = {
    storeQuestionParams.keyColumn.isEmpty
  }

  override def evaluateQuestion(): Unit = {
    storeQuestionParams.keyColumn match {
      case Some(_) => throw ParamsAlreadyDefinedException("Redis URL Already Defined")
      case None =>
        val answers = askQuestions
        storeQuestionParams.keyColumn = answers(KEY_COLUMN).toString
    }
  }
}

object RedisTableParamsQuestion {
  val KEY_COLUMN = "key_column"

  def apply(redisParams: RedisParams,
            questionExecutionContext: QuestionExecutionContext): RedisTableParamsQuestion = {
    new RedisTableParamsQuestion(redisParams, questionExecutionContext)
  }
}
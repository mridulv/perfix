package io.perfix.question.redis

import io.perfix.context.QuestionExecutionContext
import io.perfix.exceptions.ParamsAlreadyDefinedException
import io.perfix.model.{DataType, IntType, StringType}
import io.perfix.question.Question
import io.perfix.question.Question.QuestionLabel
import io.perfix.question.redis.RedisConnectionParametersQuestion.{PORT, URL}
import io.perfix.stores.redis.RedisParams

class RedisConnectionParametersQuestion(override val storeQuestionParams: RedisParams,
                                        override val questionExecutionContext: QuestionExecutionContext) extends Question {
  override val mapping: Map[QuestionLabel, DataType] = Map(
    URL -> StringType,
    PORT -> IntType
  )

  override def shouldAsk(): Boolean = {
    storeQuestionParams.url.isEmpty || storeQuestionParams.port.isEmpty
  }

  override def evaluateQuestion(): Unit = {
    storeQuestionParams.url match {
      case Some(_) => throw ParamsAlreadyDefinedException("Redis URL Already Defined")
      case None =>
        val answers = askQuestions
        storeQuestionParams.url = Some(answers(URL).toString)
        storeQuestionParams.port = Some(answers(PORT).toString.toInt)
    }
  }
}

object RedisConnectionParametersQuestion {
  val URL = "URL"
  val PORT = "PORT"

  def apply(redisParams: RedisParams,
            questionExecutionContext: QuestionExecutionContext): RedisConnectionParametersQuestion = {
    new RedisConnectionParametersQuestion(redisParams, questionExecutionContext)
  }
}
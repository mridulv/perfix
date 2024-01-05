package io.perfix.stores.redis

import io.perfix.context.QuestionExecutionContext
import io.perfix.question.{Question, Questionnaire}
import io.perfix.question.redis.{RedisConnectionParametersQuestion, RedisTableParamsQuestion}

class RedisQuestionnaire(params: RedisParams) extends Questionnaire {

  override val questions: Iterator[Question] = Iterator(
    RedisConnectionParametersQuestion(params),
    RedisTableParamsQuestion(params)
  )

}

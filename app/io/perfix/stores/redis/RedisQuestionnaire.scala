package io.perfix.stores.redis

import io.perfix.question.{Form, Questionnaire}
import io.perfix.question.redis.{RedisConnectionParametersForm, RedisTableParamsForm}

class RedisQuestionnaire(params: RedisParams) extends Questionnaire {

  override val questions: Iterator[Form] = Iterator(
    RedisConnectionParametersForm(params),
    RedisTableParamsForm(params)
  )

}

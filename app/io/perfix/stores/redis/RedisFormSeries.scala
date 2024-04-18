package io.perfix.stores.redis

import io.perfix.question.{Form, FormSeries}
import io.perfix.question.redis.{RedisConnectionParametersForm, RedisTableParamsForm}

class RedisFormSeries(params: RedisParams) extends FormSeries {

  override val forms: Iterator[Form] = Iterator(
    RedisConnectionParametersForm(params),
    RedisTableParamsForm(params)
  )

}

package io.perfix.stores.redis

import io.perfix.forms.{Form, FormSeries}
import io.perfix.forms.redis.{RedisConnectionParametersForm, RedisTableParamsForm}

class RedisFormSeries(params: RedisParams) extends FormSeries {

  override val forms: Iterator[Form] = Iterator(
    RedisConnectionParametersForm(params),
    RedisTableParamsForm(params)
  )

}

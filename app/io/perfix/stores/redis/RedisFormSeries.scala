package io.perfix.stores.redis

import io.perfix.forms.redis.{RedisConnectionParametersForm, RedisTableParamsForm}
import io.perfix.forms.{Form, FormSeries}

class RedisFormSeries(params: RedisParams) extends FormSeries {

  override val forms: Iterator[Form] = Iterator(
    RedisConnectionParametersForm(params),
    RedisTableParamsForm(params)
  )

}

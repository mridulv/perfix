package io.perfix.stores.dynamodb

import io.perfix.forms.dynamodb._
import io.perfix.forms.{Form, FormSeries}

case class DynamoDBFormSeries(params: DynamoDBParams) extends FormSeries {
  override val forms: Iterator[Form] = Iterator(
    DynamoDBTableParamsForm(params),
    DynamoDBCapacityForm(params),
    DynamoDBGSIParamsForm(params)
  )
}

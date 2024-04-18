package io.perfix.stores.dynamodb

import io.perfix.question.dynamodb._
import io.perfix.question.{Form, FormSeries}

case class DynamoDBFormSeries(params: DynamoDBParams) extends FormSeries {
  override val forms: Iterator[Form] = Iterator(
    DynamoDBTableParamsQuestions(params),
    DynamoDBCapacityQuestions(params),
    DynamoDBGSIParamsQuestions(params)
  )
}

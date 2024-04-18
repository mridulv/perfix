package io.perfix.stores.dynamodb

import io.perfix.question.dynamodb._
import io.perfix.question.{Form, Questionnaire}

case class DynamoDBQuestionnaire(params: DynamoDBParams) extends Questionnaire {
  override val questions: Iterator[Form] = Iterator(
    DynamoDBTableParamsQuestions(params),
    DynamoDBCapacityQuestions(params),
    DynamoDBGSIParamsQuestions(params)
  )
}

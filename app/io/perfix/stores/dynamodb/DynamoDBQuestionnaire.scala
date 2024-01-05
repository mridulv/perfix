package io.perfix.stores.dynamodb

import io.perfix.question.dynamodb.{DynamoDBConnectionParametersQuestions, DynamoDBTableParamsQuestions}
import io.perfix.question.{Question, Questionnaire}

case class DynamoDBQuestionnaire(params: DynamoDBParams) extends Questionnaire {
  override val questions: Iterator[Question] = Iterator(
    DynamoDBTableParamsQuestions(params),
    DynamoDBConnectionParametersQuestions(params)
  )
}

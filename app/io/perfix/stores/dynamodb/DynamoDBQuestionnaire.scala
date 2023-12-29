package io.perfix.stores.dynamodb

import io.perfix.context.QuestionExecutionContext
import io.perfix.question.dynamodb.{DynamoDBConnectionParametersQuestions, DynamoDBTableParamsQuestions}
import io.perfix.question.{Question, Questionnaire}

case class DynamoDBQuestionnaire(params: DynamoDBParams,
                                 questionExecutionContext: QuestionExecutionContext) extends Questionnaire {
  override val questions: Iterator[Question] = Iterator(
    DynamoDBTableParamsQuestions(params, questionExecutionContext),
    DynamoDBConnectionParametersQuestions(params, questionExecutionContext)
  )
}

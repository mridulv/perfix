package io.perfix.question.dynamodb

import io.perfix.context.QuestionExecutionContext
import io.perfix.exceptions.ParamsAlreadyDefinedException
import io.perfix.model.StringType
import io.perfix.question.Question
import io.perfix.question.dynamodb.DynamoDBConnectionParametersQuestions._
import io.perfix.stores.dynamodb.{DynamoDBConnectionParams, DynamoDBParams}

class DynamoDBConnectionParametersQuestions(override val storeQuestionParams: DynamoDBParams,
                                            override val questionExecutionContext: QuestionExecutionContext) extends Question {

  override def shouldAsk(): Boolean = {
    import storeQuestionParams._
    dynamoDBConnectionParams.isEmpty
  }

  override def evaluateQuestion(): Unit = {
    import storeQuestionParams._
    dynamoDBConnectionParams match {
      case Some(_) => throw ParamsAlreadyDefinedException("mySQLConnectionParams")
      case None =>
        val questionMapping = Map(
          CONNECTION_URL -> StringType,
          ACCESS_ID -> StringType,
          ACCESS_SECRET -> StringType
        )
        val answers = askQuestions(questionMapping)
        storeQuestionParams.dynamoDBConnectionParams = Some(
          DynamoDBConnectionParams(
            answers(CONNECTION_URL).toString,
            answers(ACCESS_ID).toString,
            answers(ACCESS_SECRET).toString
          )
        )
    }
  }
}

object DynamoDBConnectionParametersQuestions {
  val CONNECTION_URL = "connection_url"
  val ACCESS_ID = "access_id"
  val ACCESS_SECRET = "access_secret"

  def apply(dynamoDBParams: DynamoDBParams,
            questionExecutionContext: QuestionExecutionContext): DynamoDBConnectionParametersQuestions = {
    new DynamoDBConnectionParametersQuestions(dynamoDBParams, questionExecutionContext)
  }
}



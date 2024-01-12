package io.perfix.question.dynamodb

import DynamoDBConnectionParametersQuestions._
import io.perfix.exceptions.ParamsAlreadyDefinedException
import io.perfix.model.{DataType, QuestionType, StringType}
import io.perfix.question.Question
import io.perfix.question.Question.QuestionLabel
import io.perfix.stores.dynamodb.{DynamoDBConnectionParams, DynamoDBParams}

class DynamoDBConnectionParametersQuestions(override val storeQuestionParams: DynamoDBParams) extends Question {

  override val mapping: Map[QuestionLabel, QuestionType] = Map(
    CONNECTION_URL -> QuestionType(StringType),
    ACCESS_ID -> QuestionType(StringType),
    ACCESS_SECRET -> QuestionType(StringType)
  )

  override def shouldAsk(): Boolean = {
    import storeQuestionParams._
    dynamoDBConnectionParams.isEmpty
  }

  override def setAnswers(answers: Map[QuestionLabel, Any]): Unit = {
    import storeQuestionParams._
    dynamoDBConnectionParams match {
      case Some(_) => throw ParamsAlreadyDefinedException("mySQLConnectionParams")
      case None =>
        storeQuestionParams.dynamoDBConnectionParams = Some(
          DynamoDBConnectionParams(
            answers.get(CONNECTION_URL).map(_.toString),
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

  def apply(dynamoDBParams: DynamoDBParams): DynamoDBConnectionParametersQuestions = {
    new DynamoDBConnectionParametersQuestions(dynamoDBParams)
  }
}



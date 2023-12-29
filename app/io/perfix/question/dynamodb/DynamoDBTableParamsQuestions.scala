package io.perfix.question.dynamodb

import DynamoDBTableParamsQuestions.{PARTITION_KEY, SORT_KEY, TABLE_NAME}
import io.perfix.context.QuestionExecutionContext
import io.perfix.exceptions.ParamsAlreadyDefinedException
import io.perfix.model.{StringType, _}
import io.perfix.question.Question
import io.perfix.question.Question.QuestionLabel
import io.perfix.question.dynamodb.DynamoDBConnectionParametersQuestions.{ACCESS_ID, ACCESS_SECRET, CONNECTION_URL}
import io.perfix.stores.dynamodb.{DynamoDBParams, DynamoDBTableParams}

class DynamoDBTableParamsQuestions(override val storeQuestionParams: DynamoDBParams,
                                   override val questionExecutionContext: QuestionExecutionContext) extends Question {

  override val mapping: Map[QuestionLabel, DataType] = Map(
    TABLE_NAME -> StringType,
    PARTITION_KEY -> StringType,
    SORT_KEY -> StringType
  )

  override def shouldAsk(): Boolean = {
    import storeQuestionParams._
    dynamoDBTableParams.isEmpty
  }

  override def evaluateQuestion(): Unit = {
    import storeQuestionParams._
    dynamoDBTableParams match {
      case Some(_) => throw ParamsAlreadyDefinedException("mySQLConnectionParams")
      case None =>
        val answers = askQuestions
        storeQuestionParams.dynamoDBTableParams = Some(
          DynamoDBTableParams(
            answers(TABLE_NAME).toString,
            answers(PARTITION_KEY).toString,
            answers(SORT_KEY).toString
          )
        )
    }
  }
}

object DynamoDBTableParamsQuestions {
  val TABLE_NAME = "table_name"
  val PARTITION_KEY = "partition_key"
  val SORT_KEY = "sort_key"

  def apply(dynamoDBParams: DynamoDBParams,
            questionExecutionContext: QuestionExecutionContext): DynamoDBTableParamsQuestions = {
    new DynamoDBTableParamsQuestions(dynamoDBParams, questionExecutionContext)
  }
}


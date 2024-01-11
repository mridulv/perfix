package io.perfix.question.dynamodb

import io.perfix.exceptions.ParamsAlreadyDefinedException
import io.perfix.model.{DataType, IntType}
import io.perfix.question.Question
import io.perfix.question.Question.QuestionLabel
import io.perfix.question.dynamodb.DynamoDBCapacityQuestions.{READ_CAPACITY, WRITE_CAPACITY}
import io.perfix.stores.dynamodb.{DynamoDBCapacityParams, DynamoDBParams}

class DynamoDBCapacityQuestions(override val storeQuestionParams: DynamoDBParams) extends Question {
  override val mapping: Map[QuestionLabel, DataType] = Map(
    READ_CAPACITY -> IntType,
    WRITE_CAPACITY -> IntType
  )

  override def shouldAsk(): Boolean = {
    storeQuestionParams.dynamoDBCapacityParams.isEmpty
  }

  override def setAnswers(answers: Map[QuestionLabel, Any]): Unit = {
    import storeQuestionParams._
    dynamoDBCapacityParams match {
      case Some(_) => throw ParamsAlreadyDefinedException("dynamoDBCapacityParams")
      case None =>
        dynamoDBCapacityParams = Some(
          DynamoDBCapacityParams(
            readCapacity = answers.get(READ_CAPACITY).map(_.toString.toLong),
            writeCapacity = answers.get(WRITE_CAPACITY).map(_.toString.toLong)
          )
        )
    }
  }
}

object DynamoDBCapacityQuestions {
  val READ_CAPACITY = "read_capacity"
  val WRITE_CAPACITY = "write_capacity"

  def apply(dynamoDBParams: DynamoDBParams): DynamoDBCapacityQuestions = {
    new DynamoDBCapacityQuestions(dynamoDBParams)
  }
}
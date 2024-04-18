package io.perfix.question.dynamodb

import io.perfix.exceptions.ParamsAlreadyDefinedException
import io.perfix.model.{IntType, QuestionType}
import io.perfix.question.Form
import io.perfix.question.Form.QuestionLabel
import io.perfix.question.dynamodb.DynamoDBCapacityQuestions.{READ_CAPACITY, WRITE_CAPACITY}
import io.perfix.stores.dynamodb.{DynamoDBCapacityParams, DynamoDBParams}

class DynamoDBCapacityQuestions(override val storeQuestionParams: DynamoDBParams) extends Form {
  override val mapping: Map[QuestionLabel, QuestionType] = Map(
    READ_CAPACITY -> QuestionType(IntType, isRequired = false),
    WRITE_CAPACITY -> QuestionType(IntType, isRequired = false)
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
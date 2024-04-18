package io.perfix.question.dynamodb

import io.perfix.exceptions.ParamsAlreadyDefinedException
import io.perfix.model.{IntType, FormInputType}
import io.perfix.question.Form
import io.perfix.question.Form.FormInputName
import io.perfix.question.dynamodb.DynamoDBCapacityForm.{READ_CAPACITY, WRITE_CAPACITY}
import io.perfix.stores.dynamodb.{DynamoDBCapacityParams, DynamoDBParams}

class DynamoDBCapacityForm(override val formParams: DynamoDBParams) extends Form {
  override val mapping: Map[FormInputName, FormInputType] = Map(
    READ_CAPACITY -> FormInputType(IntType, isRequired = false),
    WRITE_CAPACITY -> FormInputType(IntType, isRequired = false)
  )

  override def shouldAsk(): Boolean = {
    formParams.dynamoDBCapacityParams.isEmpty
  }

  override def setAnswers(answers: Map[FormInputName, Any]): Unit = {
    import formParams._
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

object DynamoDBCapacityForm {
  val READ_CAPACITY = "read_capacity"
  val WRITE_CAPACITY = "write_capacity"

  def apply(dynamoDBParams: DynamoDBParams): DynamoDBCapacityForm = {
    new DynamoDBCapacityForm(dynamoDBParams)
  }
}
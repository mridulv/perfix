package io.perfix.question.dynamodb

import DynamoDBTableParamsQuestions.{CONNECTION_URL, PARTITION_KEY, SORT_KEY, TABLE_NAME}
import io.perfix.exceptions.ParamsAlreadyDefinedException
import io.perfix.model._
import io.perfix.question.Form
import io.perfix.question.Form.FormInputName
import io.perfix.stores.dynamodb.{DynamoDBParams, DynamoDBTableParams}

class DynamoDBTableParamsQuestions(override val formParams: DynamoDBParams) extends Form {

  override val mapping: Map[FormInputName, FormInputType] = Map(
    CONNECTION_URL -> FormInputType(StringType, isRequired = false),
    TABLE_NAME -> FormInputType(StringType),
    PARTITION_KEY -> FormInputType(StringType),
    SORT_KEY -> FormInputType(StringType)
  )

  override def shouldAsk(): Boolean = {
    import formParams._
    dynamoDBTableParams.isEmpty
  }

  override def setAnswers(answers: Map[FormInputName, Any]): Unit = {
    import formParams._
    dynamoDBTableParams match {
      case Some(_) => throw ParamsAlreadyDefinedException("mySQLConnectionParams")
      case None =>
        formParams.dynamoDBTableParams = Some(
          DynamoDBTableParams(
            answers.get(CONNECTION_URL).map(_.toString),
            answers(TABLE_NAME).toString,
            answers(PARTITION_KEY).toString,
            answers(SORT_KEY).toString
          )
        )
    }
  }
}

object DynamoDBTableParamsQuestions {
  val CONNECTION_URL = "connection_url"
  val TABLE_NAME = "table_name"
  val PARTITION_KEY = "partition_key"
  val SORT_KEY = "sort_key"

  def apply(dynamoDBParams: DynamoDBParams): DynamoDBTableParamsQuestions = {
    new DynamoDBTableParamsQuestions(dynamoDBParams)
  }
}


package io.perfix.question.dynamodb

import io.perfix.exceptions.ParamsAlreadyDefinedException
import io.perfix.model.{QuestionType, StringType}
import io.perfix.question.Question
import io.perfix.question.Question.QuestionLabel
import io.perfix.question.dynamodb.DynamoDBGSIParamsQuestions.GSI
import io.perfix.stores.dynamodb.model.DynamoDBGSIMetadataParams
import io.perfix.stores.dynamodb.DynamoDBParams
import play.api.libs.json.Json

case class DynamoDBGSIParamsQuestions(override val storeQuestionParams: DynamoDBParams) extends Question {

  override val mapping: Map[QuestionLabel, QuestionType] = Map(
    GSI -> QuestionType(StringType, isRequired = false)
  )

  override def shouldAsk(): Boolean = {
    import storeQuestionParams._
    dynamoDBGSIMetadataParams.isEmpty
  }

  override def setAnswers(answers: Map[QuestionLabel, Any]): Unit = {
    import storeQuestionParams._
    dynamoDBGSIMetadataParams match {
      case Some(_) => throw ParamsAlreadyDefinedException("mySQLConnectionParams")
      case None =>
        storeQuestionParams.dynamoDBGSIMetadataParams = answers.get(GSI).map { v =>
          Json.parse(v.toString).as[DynamoDBGSIMetadataParams]
        }
    }
  }
}

object DynamoDBGSIParamsQuestions {
  val GSI = "GSI"

  def apply(dynamoDBParams: DynamoDBParams): DynamoDBGSIParamsQuestions = {
    new DynamoDBGSIParamsQuestions(dynamoDBParams)
  }
}

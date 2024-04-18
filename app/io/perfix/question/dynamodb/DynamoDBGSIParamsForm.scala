package io.perfix.question.dynamodb

import io.perfix.exceptions.ParamsAlreadyDefinedException
import io.perfix.model.{FormInputType, StringType}
import io.perfix.question.Form
import io.perfix.question.Form.FormInputName
import io.perfix.question.dynamodb.DynamoDBGSIParamsForm.GSI
import io.perfix.stores.dynamodb.model.DynamoDBGSIMetadataParams
import io.perfix.stores.dynamodb.DynamoDBParams
import play.api.libs.json.Json

case class DynamoDBGSIParamsForm(override val formParams: DynamoDBParams) extends Form {

  override val mapping: Map[FormInputName, FormInputType] = Map(
    GSI -> FormInputType(StringType, isRequired = false)
  )

  override def shouldAsk(): Boolean = {
    import formParams._
    dynamoDBGSIMetadataParams.isEmpty
  }

  override def setAnswers(answers: Map[FormInputName, Any]): Unit = {
    import formParams._
    dynamoDBGSIMetadataParams match {
      case Some(_) => throw ParamsAlreadyDefinedException("mySQLConnectionParams")
      case None =>
        formParams.dynamoDBGSIMetadataParams = answers.get(GSI).map { v =>
          Json.parse(v.toString).as[DynamoDBGSIMetadataParams]
        }
    }
  }
}

object DynamoDBGSIParamsForm {
  val GSI = "GSI"

  def apply(dynamoDBParams: DynamoDBParams): DynamoDBGSIParamsForm = {
    new DynamoDBGSIParamsForm(dynamoDBParams)
  }
}

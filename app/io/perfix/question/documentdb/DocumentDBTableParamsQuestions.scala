package io.perfix.question.documentdb

import io.perfix.exceptions.ParamsAlreadyDefinedException
import io.perfix.model.{FormInputType, StringType}
import io.perfix.question.Form
import io.perfix.question.Form.FormInputName
import io.perfix.question.documentdb.DocumentDBTableParamsQuestions.COLLECTION_NAME
import io.perfix.stores.documentdb.{DocumentDBParams, DocumentDBTableParams}

class DocumentDBTableParamsQuestions(override val storeQuestionParams: DocumentDBParams) extends Form {

  override val mapping: Map[FormInputName, FormInputType] = Map(
    COLLECTION_NAME -> FormInputType(StringType)
  )

  override def shouldAsk(): Boolean = {
    storeQuestionParams.documentDBTableParams.isEmpty
  }

  override def setAnswers(answers: Map[FormInputName, Any]): Unit = {
    import storeQuestionParams._
    documentDBTableParams match {
      case Some(_) => throw ParamsAlreadyDefinedException("mySQLTableParams")
      case None => documentDBTableParams = Some(DocumentDBTableParams(answers(COLLECTION_NAME).toString))
    }
  }

}

object DocumentDBTableParamsQuestions {
  val COLLECTION_NAME = "collectionName"

  def apply(documentDBParams: DocumentDBParams): DocumentDBTableParamsQuestions = {
    new DocumentDBTableParamsQuestions(documentDBParams)
  }
}



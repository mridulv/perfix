package io.perfix.question.documentdb

import io.perfix.exceptions.ParamsAlreadyDefinedException
import io.perfix.model.{FormInputType, StringType}
import io.perfix.question.Form
import io.perfix.question.Form.FormInputName
import io.perfix.question.documentdb.DocumentDBConnectionParamsForm._
import io.perfix.stores.documentdb.{DocumentDBConnectionParams, DocumentDBParams}

class DocumentDBConnectionParamsForm(override val storeQuestionParams: DocumentDBParams) extends Form {

  override val mapping: Map[FormInputName, FormInputType] = Map(
    URL -> FormInputType(StringType),
    DATABASE -> FormInputType(StringType)
  )

  override def shouldAsk(): Boolean = {
    import storeQuestionParams._
    documentDBConnectionParams.isEmpty
  }

  override def setAnswers(answers: Map[FormInputName, Any]): Unit = {
    import storeQuestionParams._
    documentDBConnectionParams match {
      case Some(_) => throw ParamsAlreadyDefinedException("mySQLConnectionParams")
      case None =>
        documentDBConnectionParams = Some(DocumentDBConnectionParams(answers(URL).toString, answers(DATABASE).toString))
    }
  }
}

object DocumentDBConnectionParamsForm {
  val URL = "url"
  val DATABASE = "database"

  def apply(documentDBParams: DocumentDBParams): DocumentDBConnectionParamsForm = {
    new DocumentDBConnectionParamsForm(documentDBParams)
  }
}

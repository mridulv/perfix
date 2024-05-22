package io.perfix.forms.documentdb

import io.perfix.exceptions.ParamsAlreadyDefinedException
import io.perfix.forms.Form
import io.perfix.forms.Form.FormInputName
import io.perfix.forms.documentdb.DocumentDBConnectionParamsForm._
import io.perfix.model.{FormInputType, StringType}
import io.perfix.stores.documentdb.{DocumentDBConnectionParams, DocumentDBParams}

class DocumentDBConnectionParamsForm(override val formParams: DocumentDBParams) extends Form {

  override val mapping: Map[FormInputName, FormInputType] = Map(
    URL -> FormInputType(StringType),
    DATABASE -> FormInputType(StringType)
  )

  override def shouldAsk(): Boolean = {
    import formParams._
    documentDBConnectionParams.isEmpty
  }

  override def setAnswers(answers: Map[FormInputName, Any]): Unit = {
    import formParams._
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

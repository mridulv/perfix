package io.perfix.forms.documentdb

import io.perfix.exceptions.ParamsAlreadyDefinedException
import io.perfix.model.{FormInputType, StringType}
import io.perfix.forms.Form
import io.perfix.forms.Form.FormInputName
import io.perfix.forms.documentdb.DocumentDBTableParamsForm.COLLECTION_NAME
import io.perfix.stores.documentdb.{DocumentDBParams, DocumentDBTableParams}

class DocumentDBTableParamsForm(override val formParams: DocumentDBParams) extends Form {

  override val mapping: Map[FormInputName, FormInputType] = Map(
    COLLECTION_NAME -> FormInputType(StringType)
  )

  override def shouldAsk(): Boolean = {
    formParams.documentDBTableParams.isEmpty
  }

  override def setAnswers(answers: Map[FormInputName, Any]): Unit = {
    import formParams._
    documentDBTableParams match {
      case Some(_) => throw ParamsAlreadyDefinedException("mySQLTableParams")
      case None => documentDBTableParams = Some(DocumentDBTableParams(answers(COLLECTION_NAME).toString))
    }
  }

}

object DocumentDBTableParamsForm {
  val COLLECTION_NAME = "collectionName"

  def apply(documentDBParams: DocumentDBParams): DocumentDBTableParamsForm = {
    new DocumentDBTableParamsForm(documentDBParams)
  }
}



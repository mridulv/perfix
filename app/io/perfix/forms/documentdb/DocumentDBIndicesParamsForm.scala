package io.perfix.forms.documentdb

import io.perfix.exceptions.ParamsAlreadyDefinedException
import io.perfix.forms.Form
import io.perfix.forms.Form.FormInputName
import io.perfix.forms.documentdb.DocumentDBIndicesParamsForm.INDICES_COLUMNS
import io.perfix.model.{FormInputType, StringType}
import io.perfix.stores.documentdb.DocumentDBParams
import io.perfix.stores.documentdb.model.DocumentDBIndicesParams
import play.api.libs.json.Json

class DocumentDBIndicesParamsForm(override val formParams: DocumentDBParams) extends Form {

  override val mapping: Map[FormInputName, FormInputType] = Map(
    INDICES_COLUMNS -> FormInputType(StringType, isRequired = false)
  )

  override def shouldAsk(): Boolean = {
    formParams.documentDBIndicesParams.isEmpty
  }

  override def setAnswers(answers: Map[FormInputName, Any]): Unit = {
    import formParams._
    documentDBIndicesParams match {
      case Some(_) => throw ParamsAlreadyDefinedException("documentDbIndicesParams")
      case None => documentDBIndicesParams = answers.get(INDICES_COLUMNS).map { v =>
        Json.parse(v.toString).as[DocumentDBIndicesParams]
      }
    }
  }

}

object DocumentDBIndicesParamsForm {
  val INDICES_COLUMNS = "indices_columns"

  def apply(documentDBParams: DocumentDBParams): DocumentDBIndicesParamsForm = {
    new DocumentDBIndicesParamsForm(documentDBParams)
  }
}



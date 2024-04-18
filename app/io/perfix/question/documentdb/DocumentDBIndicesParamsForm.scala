package io.perfix.question.documentdb

import io.perfix.exceptions.ParamsAlreadyDefinedException
import io.perfix.model.{QuestionType, StringType}
import io.perfix.question.Form
import io.perfix.question.Form.FormInputName
import io.perfix.question.documentdb.DocumentDBIndicesParamsForm.INDICES_COLUMNS
import io.perfix.stores.documentdb.model.DocumentDBIndicesParams
import io.perfix.stores.documentdb.DocumentDBParams
import play.api.libs.json.Json

class DocumentDBIndicesParamsForm(override val storeQuestionParams: DocumentDBParams) extends Form {

  override val mapping: Map[FormInputName, QuestionType] = Map(
    INDICES_COLUMNS -> QuestionType(StringType, isRequired = false)
  )

  override def shouldAsk(): Boolean = {
    storeQuestionParams.documentDBIndicesParams.isEmpty
  }

  override def setAnswers(answers: Map[FormInputName, Any]): Unit = {
    import storeQuestionParams._
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



package io.perfix.question.documentdb

import io.perfix.exceptions.ParamsAlreadyDefinedException
import io.perfix.model.{QuestionType, StringType}
import io.perfix.question.Question
import io.perfix.question.Question.QuestionLabel
import io.perfix.question.documentdb.DocumentDBIndicesParamsQuestion.INDICES_COLUMNS
import io.perfix.stores.documentdb.model.DocumentDBIndicesParams
import io.perfix.stores.documentdb.DocumentDBParams
import play.api.libs.json.Json

class DocumentDBIndicesParamsQuestion(override val storeQuestionParams: DocumentDBParams) extends Question {

  override val mapping: Map[QuestionLabel, QuestionType] = Map(
    INDICES_COLUMNS -> QuestionType(StringType, isRequired = false)
  )

  override def shouldAsk(): Boolean = {
    storeQuestionParams.documentDBIndicesParams.isEmpty
  }

  override def setAnswers(answers: Map[QuestionLabel, Any]): Unit = {
    import storeQuestionParams._
    documentDBIndicesParams match {
      case Some(_) => throw ParamsAlreadyDefinedException("documentDbIndicesParams")
      case None => documentDBIndicesParams = answers.get(INDICES_COLUMNS).map { v =>
        Json.parse(v.toString).as[DocumentDBIndicesParams]
      }
    }
  }

}

object DocumentDBIndicesParamsQuestion {
  val INDICES_COLUMNS = "indices_columns"

  def apply(documentDBParams: DocumentDBParams): DocumentDBIndicesParamsQuestion = {
    new DocumentDBIndicesParamsQuestion(documentDBParams)
  }
}



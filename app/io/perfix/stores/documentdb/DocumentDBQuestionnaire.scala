package io.perfix.stores.documentdb

import io.perfix.question.documentdb.{DocumentDBConnectionParamsForm, DocumentDBIndicesParamsForm, DocumentDBTableParamsQuestions}
import io.perfix.question.{Form, Questionnaire}

case class DocumentDBQuestionnaire(params: DocumentDBParams) extends Questionnaire {
  override val questions: Iterator[Form] = Iterator(
    DocumentDBConnectionParamsForm(params),
    DocumentDBTableParamsQuestions(params),
    DocumentDBIndicesParamsForm(params)
  )
}

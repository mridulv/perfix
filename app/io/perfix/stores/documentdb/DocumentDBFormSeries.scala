package io.perfix.stores.documentdb

import io.perfix.question.documentdb.{DocumentDBConnectionParamsForm, DocumentDBIndicesParamsForm, DocumentDBTableParamsQuestions}
import io.perfix.question.{Form, FormSeries}

case class DocumentDBFormSeries(params: DocumentDBParams) extends FormSeries {
  override val forms: Iterator[Form] = Iterator(
    DocumentDBConnectionParamsForm(params),
    DocumentDBTableParamsQuestions(params),
    DocumentDBIndicesParamsForm(params)
  )
}

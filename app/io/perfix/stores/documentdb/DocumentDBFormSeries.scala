package io.perfix.stores.documentdb

import io.perfix.forms.documentdb.{DocumentDBConnectionParamsForm, DocumentDBIndicesParamsForm, DocumentDBTableParamsForm}
import io.perfix.forms.{Form, FormSeries}

case class DocumentDBFormSeries(params: DocumentDBParams) extends FormSeries {
  override val forms: Iterator[Form] = Iterator(
    DocumentDBConnectionParamsForm(params),
    DocumentDBTableParamsForm(params),
    DocumentDBIndicesParamsForm(params)
  )
}

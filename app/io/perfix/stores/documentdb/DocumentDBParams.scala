package io.perfix.stores.documentdb

import io.perfix.model.DataDescription
import io.perfix.question.QuestionParams

case class DocumentDBParams(dataDescription: DataDescription) extends QuestionParams {
  var documentDBConnectionParams: Option[DocumentDBConnectionParams] = None
  var documentDBTableParams: Option[DocumentDBTableParams] = None

  def isEmpty: Boolean = {
    documentDBConnectionParams.isEmpty && documentDBTableParams.isEmpty
  }
}

case class DocumentDBConnectionParams(url: String, database: String)
case class DocumentDBTableParams(tableName: String)

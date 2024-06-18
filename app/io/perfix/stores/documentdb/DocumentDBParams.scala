package io.perfix.stores.documentdb

case class DocumentDBParams() {
  var documentDBConnectionParams: Option[DocumentDBConnectionParams] = None
  var documentDBTableParams: Option[DocumentDBTableParams] = None
  var documentDBIndicesParams: Option[DocumentDBIndicesParams] = None

  def isEmpty: Boolean = {
    documentDBConnectionParams.isEmpty && documentDBTableParams.isEmpty
  }
}

case class DocumentDBConnectionParams(url: String, database: String)
case class DocumentDBTableParams(collectionName: String)

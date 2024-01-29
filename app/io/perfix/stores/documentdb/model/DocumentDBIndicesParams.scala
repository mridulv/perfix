package io.perfix.stores.documentdb.model

import play.api.libs.json.{Format, Json}

case class DocumentDBIndicesParams(columns: Seq[String])

object DocumentDBIndicesParams {
  implicit val DocumentDBIndicesParamsFormatter: Format[DocumentDBIndicesParams] = Json.format[DocumentDBIndicesParams]
}

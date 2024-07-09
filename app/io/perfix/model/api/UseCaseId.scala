package io.perfix.model.api

import play.api.libs.json.{Format, Json}

case class UseCaseId(id: Int)

object UseCaseId {
  implicit val ConversationIdFormatter: Format[UseCaseId] = Json.format[UseCaseId]
}


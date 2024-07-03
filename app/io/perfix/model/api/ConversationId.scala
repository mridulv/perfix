package io.perfix.model.api

import play.api.libs.json.{Format, Json}

case class ConversationId(id: Int)

object ConversationId {
  implicit val ConversationIdFormatter: Format[ConversationId] = Json.format[ConversationId]
}


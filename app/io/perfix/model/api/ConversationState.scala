package io.perfix.model.api

import play.api.libs.json.{Format, JsError, JsString, JsSuccess, Reads, Writes}

object ConversationState extends Enumeration {

  type ConversationState = Value
  val Created, InProgress, Completed, Failed = Value

  implicit val writes: Writes[ConversationState] = Writes[ConversationState] { conversationState =>
    JsString(conversationState.toString)
  }

  implicit val reads: Reads[ConversationState] = Reads[ConversationState] {
    case JsString(str) => JsSuccess(ConversationState.withName(str))
    case _ => JsError("Invalid value for Conversation State")
  }

  implicit val ExperimentStateFormat: Format[ConversationState] = Format(reads, writes)
}

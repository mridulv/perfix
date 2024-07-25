package io.perfix.conversations

import io.perfix.model.api.ConversationMessage

sealed trait State

case object ConversationStarted extends State
case object OverallConversationCompleted extends State
case object ConversationCompleted extends State

case class Context(currentState: State, messages: Seq[ConversationMessage] = Seq.empty)
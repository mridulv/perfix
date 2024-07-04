package io.perfix.manager

import com.google.inject.{Inject, Singleton}
import io.perfix.db.ConversationStore
import io.perfix.model.api.{ConversationId, ConversationParams}


@Singleton
class ConversationManager @Inject()(conversationStore: ConversationStore) {

  def create(conversationParams: ConversationParams): ConversationParams = {
    val createdConversationParams = conversationStore.create(conversationParams)
    val id = createdConversationParams.conversationId.map(_.id).getOrElse("")
    createdConversationParams.copy(name = Some(s"Untitled $id"))
  }

  def get(conversationId: ConversationId): Option[ConversationParams] = {
    conversationStore.get(conversationId)
  }

  def list(): Seq[ConversationParams] = {
    conversationStore.list()
  }

  def delete(conversationId: ConversationId): Int = {
    conversationStore.delete(conversationId)
  }
}

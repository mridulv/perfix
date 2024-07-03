package io.perfix.manager

import com.google.inject.{Inject, Singleton}
import io.perfix.db.ConversationStore
import io.perfix.model.api.{ConversationId, ConversationParams}


@Singleton
class ConversationManager @Inject()(conversationStore: ConversationStore) {

  def create(conversationParams: ConversationParams): ConversationParams = {
    conversationStore.create(conversationParams)
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

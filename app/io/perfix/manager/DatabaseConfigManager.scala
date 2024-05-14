package io.perfix.manager

import com.google.inject.{Inject, Singleton}
import io.perfix.exceptions.InvalidStateException
import io.perfix.model._
import io.perfix.store.DatabaseConfigStore

@Singleton
class DatabaseConfigManager @Inject()(databaseConfigStore: DatabaseConfigStore) {

  def create(databaseConfigParams: DatabaseConfigParams): DatabaseConfigId = {
    val databaseConfigId = databaseConfigStore.create(databaseConfigParams)
      .databaseConfigId
      .getOrElse(throw InvalidStateException("Invalid DatabaseConfig"))
    databaseConfigId
  }

  def get(databaseConfigId: DatabaseConfigId): DatabaseConfigParams = {
    databaseConfigStore
      .get(databaseConfigId)
      .getOrElse(throw InvalidStateException("Invalid DatabaseConfigId"))
  }

  def update(databaseConfigId: DatabaseConfigId,
             databaseConfigParams: DatabaseConfigParams): DatabaseConfigParams = {
    val exitingParams = databaseConfigStore
      .get(databaseConfigId)
      .getOrElse(throw InvalidStateException("Invalid DatabaseConfigId"))
    val updatedParams = exitingParams
      .copy(
        name = databaseConfigParams.name,
        storeName = databaseConfigParams.storeName,
        storeParams = databaseConfigParams.storeParams,
        dataStore = databaseConfigParams.dataStore
      )
    databaseConfigStore.update(databaseConfigId, updatedParams)
    updatedParams
  }

  def all(): Seq[DatabaseConfigParams] = {
    databaseConfigStore.list()
  }

  def delete(databaseConfigId: DatabaseConfigId): Unit = {
    databaseConfigStore.delete(databaseConfigId)
  }

}

package io.perfix.manager

import com.google.inject.Singleton
import io.perfix.model.DatabaseFormInput
import io.perfix.model.store.StoreType

@Singleton
class ConfigManager {

  def databases(): List[String] = {
    StoreType.values.byName.keys.toList
  }

  def databaseConfig(databaseName: String): DatabaseFormInput = {
    DatabaseFormInput.findRelevantDatabaseFormInput(StoreType.withName(databaseName))
  }

}

package io.perfix.manager

import com.google.inject.Singleton
import io.perfix.model.api.DatabaseFormInput
import io.perfix.model.store.StoreType

@Singleton
class ConfigManager {

  def databases(): List[String] = {
    StoreType.values.toList.map(_.toString)
  }

  def databaseConfig(databaseName: String): DatabaseFormInput = {
    DatabaseFormInput.findRelevantDatabaseFormInput(StoreType.withName(databaseName))
  }

}

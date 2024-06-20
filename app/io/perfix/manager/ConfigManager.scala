package io.perfix.manager

import com.google.inject.Singleton
import io.perfix.model.DatabaseCategory
import io.perfix.stores.Database.findRelevantDatabaseFormInput
import io.perfix.model.api.DatabaseFormInput
import io.perfix.model.store.StoreType
import io.perfix.stores.Database._

@Singleton
class ConfigManager {

  def databases(): List[String] = {
    StoreType.values.toList.map(_.toString)
  }

  def databaseConfig(databaseName: String): DatabaseFormInput = {
    findRelevantDatabaseFormInput(StoreType.withName(databaseName))
  }

  def categories(): Map[String, Seq[String]] = {
    allDatabases.toList.flatMap { database =>
      database.databaseCategory.map { category =>
        category -> database.name
      }
    }.groupBy(_._1).map { case (category, databases) =>
      category.toString -> databases.map(_._2.toString)
    }
  }

}

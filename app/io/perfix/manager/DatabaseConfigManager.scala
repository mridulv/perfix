package io.perfix.manager

import com.google.inject.{Inject, Singleton}
import io.perfix.exceptions.InvalidStateException
import io.perfix.model._
import io.perfix.model.api.{DatabaseConfigId, DatabaseConfigParams, DatabaseState}
import io.perfix.db.DatabaseConfigStore
import io.perfix.stores.Database
import io.perfix.stores.mysql.RDSDatabaseSetupParams

@Singleton
class DatabaseConfigManager @Inject()(databaseConfigStore: DatabaseConfigStore,
                                      datasetManager: DatasetManager) {

  def create(databaseConfigParams: DatabaseConfigParams): DatabaseConfigId = {
    val updatedDatabaseConfigParams = databaseConfigParams.databaseSetupParams match {
      case rdsDatabaseSetupParams: RDSDatabaseSetupParams => databaseConfigParams.copy(
        databaseSetupParams = rdsDatabaseSetupParams.copy(databaseType = Some(databaseConfigParams.dataStore))
      )
      case _ => databaseConfigParams
    }
    val databaseConfigId = databaseConfigStore.create(updatedDatabaseConfigParams)
      .databaseConfigId
      .getOrElse(throw InvalidStateException("Invalid DatabaseConfig"))
    databaseConfigId
  }

  def get(databaseConfigId: DatabaseConfigId): DatabaseConfigParams = {
    val databaseConfigParams = databaseConfigStore
      .get(databaseConfigId)
      .getOrElse(throw InvalidStateException("Invalid DatabaseConfigId"))
    val datasetParams = datasetManager.get(databaseConfigParams.datasetDetails.datasetId)
    databaseConfigParams.toDatabaseConfigDisplayParams(datasetParams)
  }

  def update(databaseConfigId: DatabaseConfigId,
             databaseConfigParams: DatabaseConfigParams): DatabaseConfigParams = {
    val dataset = datasetManager.get(databaseConfigParams.datasetDetails.datasetId)
    val exitingParams = databaseConfigStore
      .get(databaseConfigId)
      .getOrElse(throw InvalidStateException("Invalid DatabaseConfigId"))
    val updatedParams = exitingParams
      .copy(
        name = databaseConfigParams.name,
        databaseSetupParams = databaseConfigParams.databaseSetupParams,
        dataStore = databaseConfigParams.dataStore
      )
    databaseConfigStore.update(databaseConfigId, updatedParams)
    updatedParams.toDatabaseConfigDisplayParams(dataset)
  }

  def ensureDatabase(databaseConfigId: DatabaseConfigId): DatabaseConfigParams = {
    var databaseConfigParams = databaseConfigStore
      .get(databaseConfigId)
      .getOrElse(throw InvalidStateException("Invalid DatabaseConfigId"))
    if (Seq(DatabaseState.NotStarted, DatabaseState.Failed).contains(databaseConfigParams.databaseState)) {
      val (setupParams, databaseState) = Database.launch(databaseConfigParams.databaseSetupParams)
      databaseConfigParams = databaseConfigParams.copy(databaseSetupParams = setupParams, databaseState = databaseState)
      databaseConfigStore.update(databaseConfigId, databaseConfigParams)
    } else {
      var timeout = 30
      while (databaseConfigParams.databaseState == DatabaseState.InProgress && timeout > 0) {
        databaseConfigParams = databaseConfigStore.get(databaseConfigId)
          .getOrElse(throw InvalidStateException("Invalid DatabaseConfigId"))
        Thread.sleep(1000)
        timeout -= 1
      }
      if (timeout == 0) {
        throw new RuntimeException("Database Setup Failed")
      }
    }
    databaseConfigParams
  }

  def all(entityFilters: Seq[EntityFilter]): Seq[DatabaseConfigParams] = {
    val allDatabaseConfigs = databaseConfigStore.list()
    val allDatasets = datasetManager.all(Seq.empty)
    val allDatabaseConfigsWithDataset = allDatabaseConfigs.map(_.toDatabaseConfigDisplayParams(allDatasets))
    val databaseConfigFilters = entityFilters.collect {
      case df: DatabaseConfigFilter => df
    }
    databaseConfigFilters.foldLeft(allDatabaseConfigsWithDataset) { (allDatabaseConfigsWithDataset, entityFilter) =>
      allDatabaseConfigsWithDataset.filter(config => entityFilter.filter(config))
    }
  }

  def delete(databaseConfigId: DatabaseConfigId): Unit = {
    databaseConfigStore.delete(databaseConfigId)
  }

}

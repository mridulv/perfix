package io.perfix.manager

import com.google.inject.{Inject, Singleton}
import io.perfix.exceptions.InvalidStateException
import io.perfix.model._
import io.perfix.store.DatabaseConfigStore

@Singleton
class DatabaseConfigManager @Inject()(databaseConfigStore: DatabaseConfigStore,
                                      datasetManager: DatasetManager) {

  def create(databaseConfigParams: DatabaseConfigParams): DatabaseConfigId = {
    val databaseConfigId = databaseConfigStore.create(databaseConfigParams)
      .databaseConfigId
      .getOrElse(throw InvalidStateException("Invalid DatabaseConfig"))
    databaseConfigId
  }

  def get(databaseConfigId: DatabaseConfigId): DatabaseConfigDisplayParams = {
    val datasets = datasetManager.all(Seq.empty)
    databaseConfigStore
      .get(databaseConfigId)
      .getOrElse(throw InvalidStateException("Invalid DatabaseConfigId"))
      .toDatabaseConfigDisplayParams(datasets)
  }

  def update(databaseConfigId: DatabaseConfigId,
             databaseConfigParams: DatabaseConfigParams): DatabaseConfigDisplayParams = {
    val datasets = datasetManager.all(Seq.empty)
    val exitingParams = databaseConfigStore
      .get(databaseConfigId)
      .getOrElse(throw InvalidStateException("Invalid DatabaseConfigId"))
    val updatedParams = exitingParams
      .copy(
        name = databaseConfigParams.name,
        storeParams = databaseConfigParams.storeParams,
        dataStore = databaseConfigParams.dataStore
      )
    databaseConfigStore.update(databaseConfigId, updatedParams)
    updatedParams.toDatabaseConfigDisplayParams(datasets)
  }

  def all(entityFilters: Seq[EntityFilter]): Seq[DatabaseConfigDisplayParams] = {
    val allDatabaseConfigs = databaseConfigStore.list()
    val datasets = datasetManager.all(Seq.empty)
    val allDatasets = datasetManager.all(Seq.empty)
    allDatabaseConfigs.filter { databaseConfig =>
      val relevantDataset = allDatasets.find(_.id.get == databaseConfig.datasetId).get

      val datasetFilters = entityFilters.collect {
        case df: DatasetFilter => df
      }
      val databaseConfigFilters = entityFilters.collect {
        case df: DatabaseConfigFilter => df
      }
      val cond1 = datasetFilters.forall(df => df.filterDataset(relevantDataset.dataset))
      val cond2 = databaseConfigFilters.forall(df => df.filterDatabaseConfig(databaseConfig))
      cond1 && cond2
    }.map(_.toDatabaseConfigDisplayParams(datasets))
  }

  def delete(databaseConfigId: DatabaseConfigId): Unit = {
    databaseConfigStore.delete(databaseConfigId)
  }

}

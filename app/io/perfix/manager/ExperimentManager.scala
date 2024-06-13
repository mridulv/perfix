package io.perfix.manager

import com.google.inject.Inject
import io.perfix.common.ExperimentExecutor
import io.perfix.exceptions.InvalidStateException
import io.perfix.model.experiment.{ExperimentDisplayParams, ExperimentId, ExperimentParams}
import io.perfix.model.{DatabaseConfigFilter, DatasetFilter, EntityFilter, ExperimentFilter}
import io.perfix.store.ExperimentStore

import javax.inject.Singleton

@Singleton
class ExperimentManager @Inject()(datasetManager: DatasetManager,
                                  experimentStore: ExperimentStore,
                                  databaseConfigManager: DatabaseConfigManager) {

  def create(experimentParams: ExperimentParams): ExperimentId = {
    experimentStore
      .create(experimentParams)
      .experimentId
      .getOrElse(throw InvalidStateException("Invalid ExperimentParams"))
  }

  def get(experimentId: ExperimentId): ExperimentDisplayParams = {
    val experimentParams = experimentStore
      .get(experimentId)
      .getOrElse(throw InvalidStateException(s"Invalid ExperimentId ${experimentId}"))
    val databaseConfigParams = databaseConfigManager.get(experimentParams.databaseConfigId)
    val dataset = datasetManager.get(databaseConfigParams.datasetId)
    experimentParams.toExperimentDisplayParams(dataset.name, databaseConfigParams.name)
  }

  def all(entityFilters: Seq[EntityFilter]): Seq[ExperimentDisplayParams] = {
    val allExperiments = experimentStore.list()
    val allDatasets = datasetManager.all(Seq.empty)
    val allDatabaseConfigs = databaseConfigManager.all(Seq.empty)

    allExperiments.flatMap { experiment =>
      val relevantDatabaseConfig = allDatabaseConfigs.find(_.databaseConfigId.get == experiment.databaseConfigId).get
      val relevantDataset = allDatasets.find(_.id.get == relevantDatabaseConfig.datasetId).get

      val datasetFilters = entityFilters.collect {
        case df: DatasetFilter => df
      }
      val databaseConfigFilters = entityFilters.collect {
        case df: DatabaseConfigFilter => df
      }
      val experimentFilters = entityFilters.collect {
        case df: ExperimentFilter => df
      }

      val cond1 = datasetFilters.forall(df => df.filterDataset(relevantDataset.dataset))
      val cond2 = databaseConfigFilters.forall(df => df.filterDatabaseConfig(relevantDatabaseConfig.toDatabaseConfigParams))
      val cond3 = experimentFilters.forall(df => df.filterExperiment(experiment))
      if (cond1 && cond2 && cond3) {
        Some(experiment.toExperimentDisplayParams(relevantDataset.name, relevantDatabaseConfig.name))
      } else {
        None
      }
    }
  }

  def update(experimentId: ExperimentId, experimentParams: ExperimentParams): ExperimentDisplayParams = {
    experimentStore.update(experimentId, experimentParams)
    val databaseConfigParams = databaseConfigManager.get(experimentParams.databaseConfigId)
    val dataset = datasetManager.get(databaseConfigParams.datasetId)
    experimentParams.toExperimentDisplayParams(dataset.name, databaseConfigParams.name)
  }

  def executeExperiment(experimentId: ExperimentId): ExperimentDisplayParams = {
    val experimentParams = get(experimentId).toExperimentParams
    val databaseConfigParams = databaseConfigManager.get(experimentParams.databaseConfigId)
    val dataset = datasetManager.get(databaseConfigParams.datasetId).dataset
    val experimentExecutor = new ExperimentExecutor(
      experimentParams,
      databaseConfigParams.toDatabaseConfigParams,
      dataset
    )
    val result = experimentExecutor.runExperiment()
    val updatedExperimentParams = experimentParams.copy(experimentResult = Some(result))
    experimentStore.update(experimentId, updatedExperimentParams)
    experimentExecutor.cleanUp()
    updatedExperimentParams.toExperimentDisplayParams(dataset.params.name, databaseConfigParams.name)
  }

  def delete(experimentId: ExperimentId): Unit = {
    experimentStore.delete(experimentId)
  }
}
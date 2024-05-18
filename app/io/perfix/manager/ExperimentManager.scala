package io.perfix.manager

import com.google.inject.Inject
import io.perfix.common.ExperimentExecutor
import io.perfix.exceptions.InvalidStateException
import io.perfix.model.experiment.ExperimentResult.TestExperimentResult
import io.perfix.model.{DatabaseConfigFilter, DatasetFilter, EntityFilter, ExperimentFilter}
import io.perfix.model.experiment.{ExperimentId, ExperimentParams}
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

  def get(experimentId: ExperimentId): ExperimentParams = {
    experimentStore
      .get(experimentId)
      .getOrElse(throw InvalidStateException(s"Invalid ExperimentId ${experimentId}"))
      .copy(experimentResult = Some(TestExperimentResult))
  }

  def all(entityFilters: Seq[EntityFilter]): Seq[ExperimentParams] = {
    val allExperiments = experimentStore.list()
    val allDatasets = datasetManager.all(Seq.empty)
    val allDatabaseConfigs = databaseConfigManager.all(Seq.empty)

    allExperiments.filter { experiment =>
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
      val cond2 = databaseConfigFilters.forall(df => df.filterDatabaseConfig(relevantDatabaseConfig))
      val cond3 = experimentFilters.forall(df => df.filterExperiment(experiment))
      cond1 && cond2 && cond3
    }.map(_.copy(experimentResult = Some(TestExperimentResult)))
  }

  def update(experimentId: ExperimentId, experimentParams: ExperimentParams): ExperimentParams = {
    experimentStore.update(experimentId, experimentParams)
    experimentParams
  }

  def executeExperiment(experimentId: ExperimentId): ExperimentParams = {
    val experimentParams = get(experimentId)
    val databaseConfigParams = databaseConfigManager.get(experimentParams.databaseConfigId)
    val dataset = datasetManager.get(databaseConfigParams.datasetId).dataset
    val experimentExecutor = new ExperimentExecutor(
      databaseConfigParams.dataStore.name,
      experimentParams,
      databaseConfigParams,
      dataset
    )
    val result = experimentExecutor.runExperiment()
    val updatedExperimentParams = experimentParams.copy(experimentResult = Some(result))
    experimentStore.update(experimentId, updatedExperimentParams)
    experimentExecutor.cleanUp()
    updatedExperimentParams
  }

  def delete(experimentId: ExperimentId): Unit = {
    experimentStore.delete(experimentId)
  }
}
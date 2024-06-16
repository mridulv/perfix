package io.perfix.manager

import com.google.inject.Inject
import io.perfix.common.ExperimentExecutor
import io.perfix.exceptions.InvalidStateException
import io.perfix.model.experiment.{ExperimentId, ExperimentParams, MultipleExperimentResult}
import io.perfix.model.{EntityFilter, ExperimentFilter}
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
    val experimentParams = experimentStore
      .get(experimentId)
      .getOrElse(throw InvalidStateException(s"Invalid ExperimentId ${experimentId}"))
    val databaseConfigParams = databaseConfigManager.all(Seq.empty)
    val dataset = datasetManager.all(Seq.empty)
    experimentParams.toExperimentParamsWithDatabaseDetails(dataset, databaseConfigParams)
  }

  def all(entityFilters: Seq[EntityFilter]): Seq[ExperimentParams] = {
    val allDatasets = datasetManager.all(Seq.empty)
    val allDatabaseConfigs = databaseConfigManager.all(Seq.empty)
    val allExperimentsWithDatabaseDetails = experimentStore
      .list()
      .map(_.toExperimentParamsWithDatabaseDetails(allDatasets, allDatabaseConfigs))
    val experimentFilters = entityFilters.collect {
      case df: ExperimentFilter => df
    }
    experimentFilters.foldLeft(allExperimentsWithDatabaseDetails) { (experimentParams, experimentFilter) =>
      experimentParams.filter(d => experimentFilter.filter(d))
    }
  }

  def update(experimentId: ExperimentId, experimentParams: ExperimentParams): ExperimentParams = {
    experimentStore.update(experimentId, experimentParams)
    val databaseConfigParams = databaseConfigManager.all(Seq.empty)
    val dataset = datasetManager.all(Seq.empty)
    experimentParams.toExperimentParamsWithDatabaseDetails(dataset, databaseConfigParams)
  }

  def executeExperiment(experimentId: ExperimentId): ExperimentParams = {
    val experimentParams = get(experimentId)
    val allDatabaseConfigParams = databaseConfigManager.all(Seq.empty)
    val allDatasetParams = datasetManager.all(Seq.empty)
    val results = experimentParams.databaseConfigs.map { databaseConfigDetail =>
      val configParams = allDatabaseConfigParams
        .find(_.databaseConfigId.get == databaseConfigDetail.databaseConfigId)
        .getOrElse(throw InvalidStateException("Invalid ExperimentParams"))
      val datasetParams = allDatasetParams
        .find(_.id.get == configParams.datasetDetails.datasetId)
        .getOrElse(throw InvalidStateException("Invalid ExperimentParams"))
      val experimentExecutor = new ExperimentExecutor(
        experimentParams,
        configParams,
        datasetParams.dataset
      )
      val result = experimentExecutor.runExperiment()
      experimentExecutor.cleanUp()
      result
    }
    val updatedExperimentParams = experimentParams
      .copy(experimentResult = Some(MultipleExperimentResult(results)))
    experimentStore.update(experimentId, updatedExperimentParams)
    updatedExperimentParams.toExperimentParamsWithDatabaseDetails(allDatasetParams, allDatabaseConfigParams)
  }

  def delete(experimentId: ExperimentId): Unit = {
    experimentStore.delete(experimentId)
  }
}
package io.perfix.manager

import com.google.inject.Inject
import io.perfix.common.ExperimentExecutor
import io.perfix.exceptions.InvalidStateException
import io.perfix.model.experiment.{ExperimentId, ExperimentParams, ExperimentResultWithDatabaseConfigDetails}
import io.perfix.model.{EntityFilter, ExperimentFilter}
import io.perfix.db.ExperimentStore

import javax.inject.Singleton

@Singleton
class ExperimentManager @Inject()(datasetManager: DatasetManager,
                                  experimentStore: ExperimentStore,
                                  databaseConfigManager: DatabaseConfigManager) {

  def create(experimentParams: ExperimentParams): ExperimentId = {
    val databaseConfigParams = databaseConfigManager.all(Seq.empty)
    val dataset = datasetManager.all(Seq.empty)
    if (experimentParams.toExperimentParamsWithDatabaseDetails(dataset, databaseConfigParams).validateExperimentParams) {
      experimentStore
        .create(experimentParams)
        .experimentId
        .getOrElse(throw InvalidStateException("Invalid ExperimentParams"))
    } else {
      throw InvalidStateException("Choose All Databases of same database category")
    }
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
    val databaseConfigParams = databaseConfigManager.all(Seq.empty)
    val dataset = datasetManager.all(Seq.empty)
    val updatedExperimentParams = experimentParams.toExperimentParamsWithDatabaseDetails(dataset, databaseConfigParams)
    if (updatedExperimentParams.validateExperimentParams) {
      experimentStore.update(experimentId, experimentParams)
      updatedExperimentParams
    } else {
      throw InvalidStateException("Choose All Databases of same database category")
    }
  }

  def executeExperiment(experimentId: ExperimentId): ExperimentParams = {
    val experimentParams = get(experimentId)
    val allDatabaseConfigParams = databaseConfigManager.all(Seq.empty)
    val allDatasetParams = datasetManager.all(Seq.empty)
    val results = experimentParams.databaseConfigs.map { databaseConfigDetail =>
      val configParams = allDatabaseConfigParams
        .find(_.databaseConfigId.get == databaseConfigDetail.databaseConfigId)
        .getOrElse(throw InvalidStateException("Invalid ExperimentParams"))
      databaseConfigManager.ensureDatabase(databaseConfigDetail.databaseConfigId)
      val datasetParams = allDatasetParams
        .find(_.id.get == configParams.datasetDetails.datasetId)
        .getOrElse(throw InvalidStateException("Invalid ExperimentParams"))
      val experimentExecutor = new ExperimentExecutor(
        experimentParams,
        configParams,
        datasetParams
      )
      val result = experimentExecutor.runExperiment()
      experimentExecutor.cleanUp()
      ExperimentResultWithDatabaseConfigDetails(databaseConfigDetail, result)
    }
    val updatedExperimentParams = experimentParams.copy(experimentResults = Some(results))
    experimentStore.update(experimentId, updatedExperimentParams)
    updatedExperimentParams.toExperimentParamsWithDatabaseDetails(allDatasetParams, allDatabaseConfigParams)
  }

  def delete(experimentId: ExperimentId): Unit = {
    experimentStore.delete(experimentId)
  }
}
package io.perfix.manager

import com.google.inject.Inject
import io.perfix.common.ExperimentExecutor
import io.perfix.exceptions.InvalidStateException
import io.perfix.model.experiment.{ExperimentId, ExperimentParams, ExperimentResultWithDatabaseConfigDetails}
import io.perfix.model.{DatabaseCategory, EntityFilter, ExperimentFilter}
import io.perfix.db.ExperimentStore
import io.perfix.model.api.{DatabaseConfigDetails, DatabaseConfigId, DatasetDetails, DatasetId}
import io.perfix.stores.Database
import io.perfix.stores.mysql.RDSDatabaseSetupParams

import javax.inject.Singleton

@Singleton
class ExperimentManager @Inject()(datasetManager: DatasetManager,
                                  experimentStore: ExperimentStore,
                                  databaseConfigManager: DatabaseConfigManager) {

  def create(experimentParams: ExperimentParams): ExperimentId = {
    val validationErrors = experimentParams.validate()
    if (validationErrors.isEmpty) {
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
    } else {
      throw InvalidStateException(s"Validation failed: ${validationErrors.mkString(",")}")
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

  def datasets(category: String): Set[DatasetDetails] = {
    val relevantDatabases = Database.allDatabases.filter(db => db.databaseCategory.map(_.toString).contains(category))
    databaseConfigManager
      .all(Seq.empty)
      .filter { dbConfig =>
        relevantDatabases.map(_.name).contains(dbConfig.dataStore)
      }.map(_.datasetDetails).toSet
  }

  def sqlPlaceholderQueryString(databaseConfigIdOpt: Option[DatabaseConfigId]): String = {
    databaseConfigIdOpt match {
      case Some(databaseConfigId) =>
        val databaseConfigParams = databaseConfigManager.get(databaseConfigId)
        val datasetParams = datasetManager.get(databaseConfigParams.datasetDetails.datasetId)
        databaseConfigParams.databaseSetupParams match {
          case rdsDatabaseSetupParams: RDSDatabaseSetupParams =>
            val columnDescription = datasetParams.columns.getOrElse(Seq.empty).headOption
            columnDescription match {
              case Some(column) => column.columnType.getValue match {
                case str: String => s"select * from ${rdsDatabaseSetupParams.tableName} where ${column.columnName} = \"$str\""
                case v: Any => s"select * from ${rdsDatabaseSetupParams.tableName} where ${column.columnName} = $v"
              }
              case None => s"select * from ${rdsDatabaseSetupParams.tableName}"
            }
          case _ => "select * from $replace_with_the_table_name"
        }
      case None => "select * from $replace_with_the_table_name"
    }
  }

  def configs(category: String, datasetId: DatasetId): Seq[DatabaseConfigDetails] = {
    val relevantDatabases = Database.allDatabases.filter(db => db.databaseCategory.map(_.toString).contains(category))
    databaseConfigManager
      .all(Seq.empty)
      .filter { dbConfig =>
        relevantDatabases.map(_.name).contains(dbConfig.dataStore) && dbConfig.datasetDetails.datasetId == datasetId
      }.map(_.toDatabaseConfigDetails)
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
    val validationErrors = experimentParams.validate()
    if (validationErrors.isEmpty) {
      val databaseConfigParams = databaseConfigManager.all(Seq.empty)
      val dataset = datasetManager.all(Seq.empty)
      val updatedExperimentParams = experimentParams.toExperimentParamsWithDatabaseDetails(dataset, databaseConfigParams)
      if (updatedExperimentParams.validateExperimentParams) {
        experimentStore.update(experimentId, experimentParams)
        updatedExperimentParams
      } else {
        throw InvalidStateException("Choose All Databases of same database category")
      }
    } else {
      throw InvalidStateException(s"Validation failed: ${validationErrors.mkString(",")}")
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
      val updatedConfigParams = databaseConfigManager.ensureDatabase(databaseConfigDetail.databaseConfigId)
      val datasetParams = allDatasetParams
        .find(_.id.get == updatedConfigParams.datasetDetails.datasetId)
        .getOrElse(throw InvalidStateException("Invalid ExperimentParams"))
      val experimentExecutor = new ExperimentExecutor(
        experimentParams,
        updatedConfigParams,
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
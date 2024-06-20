package io.perfix.common

import io.perfix.experiment.SimplePerformanceExperiment
import io.perfix.model.experiment.{ExperimentParams, SingleExperimentResult}
import io.perfix.model.api.{DatabaseConfigParams, Dataset}
import io.perfix.stores.Database
import io.perfix.stores.documentdb.{DocumentDBDatabaseSetupParams, DocumentDBStore}
import io.perfix.stores.dynamodb.{DynamoDBDatabaseSetupParams, DynamoDBStore}
import io.perfix.stores.mysql.{MySQLDatabaseSetupParams, MySQLStore}
import io.perfix.stores.redis.{RedisDatabaseSetupParams, RedisStore}

class ExperimentExecutor(experimentParams: ExperimentParams,
                         databaseConfigParams: DatabaseConfigParams,
                         dataset: Dataset) {

  private val dataStore = Database.getStore(databaseConfigParams, dataset)

  private val experiment = new SimplePerformanceExperiment(dataStore,
    experimentParams,
    dataset
  )

  def runExperiment(): SingleExperimentResult = {
    experiment.init()
    experiment.run()
  }

  def cleanUp(): Unit = {
    experiment.cleanup()
  }

}


package io.perfix.common

import io.perfix.experiment.SimplePerformanceExperiment
import io.perfix.model.Database
import io.perfix.model.experiment.{ExperimentParams, ExperimentResult}
import io.perfix.model.api.{DatabaseConfigParams, Dataset}
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

  def runExperiment(): ExperimentResult = {
    experiment.init()
    experiment.run()
  }

  def cleanUp(): Unit = {
    experiment.cleanup()
  }

}


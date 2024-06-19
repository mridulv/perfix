package io.perfix.common

import io.perfix.experiment.SimplePerformanceExperiment
import io.perfix.model.experiment.{ExperimentParams, ExperimentResult}
import io.perfix.model.api.{DatabaseConfigParams, Dataset}
import io.perfix.stores.documentdb.{DocumentDBStore, DocumentDBDatabaseConfigParams}
import io.perfix.stores.dynamodb.{DynamoDBStore, DynamoDBDatabaseConfigParams}
import io.perfix.stores.mysql.{MySQLStore, MySQLDatabaseConfigParams}
import io.perfix.stores.redis.{RedisStore, RedisDatabaseConfigParams}

class ExperimentExecutor(experimentParams: ExperimentParams,
                         databaseConfigParams: DatabaseConfigParams,
                         dataset: Dataset) {

  private val dataStore = databaseConfigParams.storeParams match {
    case storeParams: MySQLDatabaseConfigParams => new MySQLStore(dataset.params, storeParams)
    case storeParams: DynamoDBDatabaseConfigParams => new DynamoDBStore(dataset.params, storeParams)
    case storeParams: DocumentDBDatabaseConfigParams => new DocumentDBStore(storeParams)
    case storeParams: RedisDatabaseConfigParams => new RedisStore(storeParams)
  }

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


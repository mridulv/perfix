package io.perfix.common

import io.perfix.experiment.SimplePerformanceExperiment
import io.perfix.model.experiment.{ExperimentParams, ExperimentResult}
import io.perfix.model.{DatabaseConfigParams, Dataset}
import io.perfix.stores.documentdb.{DocumentDBStore, DocumentDBStoreParams}
import io.perfix.stores.dynamodb.{DynamoDBStore, DynamoDBStoreParams}
import io.perfix.stores.mysql.{MySQLStore, MySQLStoreParams}
import io.perfix.stores.redis.{RedisStore, RedisStoreParams}

class ExperimentExecutor(experimentParams: ExperimentParams,
                         databaseConfigParams: DatabaseConfigParams,
                         dataset: Dataset) {

  private val dataStore = databaseConfigParams.storeParams match {
    case storeParams: MySQLStoreParams => new MySQLStore(dataset.params, storeParams)
    case storeParams: DynamoDBStoreParams => new DynamoDBStore(dataset.params, storeParams)
    case storeParams: DocumentDBStoreParams => new DocumentDBStore(dataset.params, storeParams)
    case storeParams: RedisStoreParams => new RedisStore(dataset.params, storeParams)
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


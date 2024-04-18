package io.perfix.common

import io.perfix.experiment.SimplePerformanceExperiment
import io.perfix.model.{ExperimentRunParams, ExperimentResult}
import io.perfix.stores.DataStore
import io.perfix.stores.documentdb.DocumentDBStore
import io.perfix.stores.dynamodb.DynamoDBStore
import io.perfix.stores.mysql.MySQLStore
import io.perfix.stores.redis.RedisStore

class ExperimentExecutor(storeName: String) {

  private val dataStore = ExperimentExecutor.getDataStore(storeName)
  private val experiment = new SimplePerformanceExperiment(dataStore)
  private val formSeriesEvaluator = new FormSeriesEvaluator(experiment.questions())

  def getQuestionnaireExecutor: FormSeriesEvaluator = {
    formSeriesEvaluator
  }

  def runExperiment(): ExperimentResult = {
    experiment.init()
    experiment.run()
  }

  def repopulateExperimentParams(experimentRunParams: ExperimentRunParams): Unit = {
    experiment.repopulateExperimentParams(experimentRunParams)
  }

  def cleanUp(): Unit = {
    experiment.cleanup()
  }

}

object ExperimentExecutor {
  def getDataStore(storeName: String): DataStore = {
    DataStore.withName(storeName) match {
      case DataStore.DynamoDBStore => new DynamoDBStore
      case DataStore.MySQLStore => new MySQLStore
      case DataStore.RedisStore => new RedisStore
      case DataStore.MongoDBStore => new DocumentDBStore
    }
  }
}

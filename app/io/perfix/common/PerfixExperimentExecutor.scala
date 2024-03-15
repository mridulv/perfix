package io.perfix.common

import io.perfix.experiment.SimplePerformanceExperiment
import io.perfix.model.{ExperimentRunParams, PerfixExperimentResult}
import io.perfix.query.{PerfixQuery, PerfixQueryFilter}
import io.perfix.stores.DataStore
import io.perfix.stores.documentdb.DocumentDBStore
import io.perfix.stores.dynamodb.DynamoDBStore
import io.perfix.stores.mysql.MySQLStore
import io.perfix.stores.redis.RedisStore

class PerfixExperimentExecutor(storeName: String) {

  private val dataStore = getDataStore(storeName)
  private val experiment = new SimplePerformanceExperiment(dataStore)
  private val questionnaireExecutor = new PerfixQuestionnaireExecutor(experiment.questions())

  def getQuestionnaireExecutor: PerfixQuestionnaireExecutor = {
    questionnaireExecutor
  }

  def runExperiment(): PerfixExperimentResult = {
    experiment.init()
    experiment.run()
  }

  def repopulateExperimentParams(experimentRunParams: ExperimentRunParams): Unit = {
    experiment.repopulateExperimentParams(experimentRunParams)
  }

  def cleanUp(): Unit = {
    experiment.cleanup()
  }

  private def getDataStore(storeName: String): DataStore = {
    DataStore.withName(storeName) match {
      case DataStore.DynamoDBStore => new DynamoDBStore
      case DataStore.MySQLStore => new MySQLStore
      case DataStore.RedisStore => new RedisStore
      case DataStore.MongoDBStore => new DocumentDBStore
    }
  }

}

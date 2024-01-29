package io.perfix.common

import io.perfix.experiment.SimplePerformanceExperiment
import io.perfix.query.{PerfixQuery, PerfixQueryFilter}
import io.perfix.stores.DataStore
import io.perfix.stores.documentdb.DocumentDBStore
import io.perfix.stores.dynamodb.DynamoDBStore
import io.perfix.stores.mysql.MySQLStore
import io.perfix.stores.redis.RedisStore

class PerfixExperimentExecutor(storeName: String) {

  private val perfixQuery = PerfixQuery(
    filtersOpt = Some(List(PerfixQueryFilter("student_name", "John"))),
    projectedFieldsOpt = Some(List("student_name")),
    limitOpt = Some(10)
  )
  private val dataStore = getDataStore(storeName)
  private val experiment = new SimplePerformanceExperiment(dataStore, perfixQuery)
  private val questionnaireExecutor = new PerfixQuestionnaireExecutor(experiment.questions())

  def getQuestionnaireExecutor: PerfixQuestionnaireExecutor = {
    questionnaireExecutor
  }

  def runExperiment(): Unit = {
    experiment.init()
    experiment.run()
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

package io.perfix.experiment

import io.perfix.BenchmarkUtil
import io.perfix.context.QuestionExecutionContext
import io.perfix.model.ExperimentParams
import io.perfix.query.{PerfixQuery, PerfixQueryFilter}
import io.perfix.question.Questionnaire
import io.perfix.stores.DataStore

class SimplePerformanceExperiment(dataStore: DataStore,
                                  questionExecutionContext: QuestionExecutionContext) {

  private val experimentParams = new ExperimentParams

  def questions(): Questionnaire = {
    val questionnaire = new ExperimentQuestionnaire(experimentParams, dataStore, questionExecutionContext)
    questionnaire
  }

  def init(): Unit = {
    if (experimentParams.isDefined) {
      dataStore.connectAndInitialize()
    } else {
      throw new Exception("Parameters not defined correctly")
    }
  }

  def run(): Unit = {
    val perfixQuery = PerfixQuery(
      filtersOpt = Some(List(PerfixQueryFilter("name", "John"))),
      projectedFieldsOpt = Some(List("name")),
      limitOpt = Some(10)
    )
    val query = dataStore.convertQuery(perfixQuery)
    dataStore.putData()
    BenchmarkUtil.runBenchmark(
      concurrentThreads = experimentParams.concurrentQueries,
      benchmarkTimeSeconds = 15,
      runTask = () => dataStore.readData(query)
    )
  }

  def cleanup(): Unit = {
    dataStore.cleanup()
  }
}

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
      filtersOpt = Some(List(PerfixQueryFilter("name2", "John"))),
      projectedFieldsOpt = Some(List("name2")),
      limitOpt = Some(10)
    )
    dataStore.putData()
    BenchmarkUtil.runBenchmark(
      concurrentThreads = experimentParams.concurrentQueries,
      benchmarkTimeSeconds = 15,
      runTask = () => dataStore.readData(perfixQuery)
    )
  }

  def cleanup(): Unit = {
    dataStore.cleanup()
  }
}

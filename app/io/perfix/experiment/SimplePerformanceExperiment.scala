package io.perfix.experiment

import io.perfix.model.ExperimentParams
import io.perfix.query.PerfixQuery
import io.perfix.question.Questionnaire
import io.perfix.stores.DataStore
import io.perfix.util.BenchmarkUtil

class SimplePerformanceExperiment(dataStore: DataStore,
                                  perfixQuery: PerfixQuery) {

  private val experimentParams = new ExperimentParams

  def questions(): Questionnaire = {
    val questionnaire = new ExperimentQuestionnaire(experimentParams, dataStore)
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

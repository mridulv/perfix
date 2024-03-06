package io.perfix.experiment

import io.perfix.model.{ExperimentParams, PerfixExperimentResult}
import io.perfix.query.PerfixQuery
import io.perfix.question.Questionnaire
import io.perfix.stores.DataStore
import io.perfix.util.BenchmarkUtil

class SimplePerformanceExperiment(dataStore: DataStore,
                                  perfixQuery: PerfixQuery) {

  private[experiment] val experimentParams = new ExperimentParams

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

  def run(): PerfixExperimentResult = {
    var rowsCount = 0
    experimentParams.dataDescription.data.grouped(experimentParams.writeBatchSize).foreach { rows =>
      try {
        dataStore.putData(rows)
      } catch {
        case e: Exception =>
          println(s"Error while adding data: ${e.getMessage}")
      }
      rowsCount += rows.size
      println(s"Added $rowsCount to the store")
    }
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

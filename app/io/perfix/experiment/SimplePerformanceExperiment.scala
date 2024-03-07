package io.perfix.experiment

import io.perfix.model.{ExperimentParams, PerfixExperimentResult}
import io.perfix.query.PerfixQuery
import io.perfix.question.Questionnaire
import io.perfix.stores.DataStore
import io.perfix.util.BenchmarkUtil

import scala.collection.mutable.ListBuffer

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
    val writeTimes = ListBuffer[Long]()
    experimentParams.dataDescription.data.grouped(experimentParams.writeBatchSize).foreach { rows =>
      try {
        val start = System.currentTimeMillis()
        dataStore.putData(rows)
        val end = System.currentTimeMillis()
        writeTimes.append(end - start)
      } catch {
        case e: Exception =>
          println(s"Error while adding data: ${e.getMessage}")
      }
      rowsCount += rows.size
      println(s"Added $rowsCount to the store")
    }
    val results = try {
      println(s"Starting with the experiment")
      val results = BenchmarkUtil.runBenchmark(
        concurrentThreads = experimentParams.concurrentQueries,
        benchmarkTimeSeconds = 15,
        runTask = () => dataStore.readData(perfixQuery)
      )
      println(results)
      results
    } catch {
      case e: Exception =>
        println(s"Error while running benchmark: ${e.getMessage}")
        throw e
    }
    results.copy(
      overallWriteTimeTaken = writeTimes.sum,
      writeLatencies = BenchmarkUtil.printPercentiles(writeTimes.toSeq)
    )
  }

  def cleanup(): Unit = {
    dataStore.cleanup()
  }
}

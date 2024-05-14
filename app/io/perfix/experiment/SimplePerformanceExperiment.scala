package io.perfix.experiment

import io.perfix.model.Dataset
import io.perfix.model.experiment.{ExperimentParams, ExperimentResult}
import io.perfix.model.store.StoreParams
import io.perfix.stores.DataStore
import io.perfix.util.BenchmarkUtil

import scala.collection.mutable.ListBuffer

class SimplePerformanceExperiment[T <: StoreParams](dataStore: DataStore[T],
                                                    experimentParams: ExperimentParams,
                                                    dataset: Dataset) extends Experiment {

  def init(): Unit = {
    dataStore.launcher().foreach(_.launch())
    dataStore.connectAndInitialize()
  }

  def run(): ExperimentResult = {
    var rowsCount = 0
    val writeTimes = ListBuffer[Long]()
    dataset.data.grouped(experimentParams.writeBatchSize).foreach { rows =>
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
        benchmarkTimeSeconds = experimentParams.experimentTimeInSeconds,
        runTask = () => dataStore.readData(experimentParams.query).size
      )
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

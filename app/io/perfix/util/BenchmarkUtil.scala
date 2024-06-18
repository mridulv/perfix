package io.perfix.util

import io.perfix.model.experiment.{ExperimentResult, PercentileLatency, SingleExperimentResult}

import java.util.concurrent.{Callable, Executors, Future, TimeUnit}
import scala.collection.mutable.ListBuffer

object BenchmarkUtil {

  def runBenchmark(concurrentThreads: Int, benchmarkTimeSeconds: Long, runTask: () => Int): SingleExperimentResult = {
    val executor = Executors.newFixedThreadPool(concurrentThreads)
    val futures = new ListBuffer[Future[List[Long]]]
    val startTime = System.currentTimeMillis()
    val numResults = scala.collection.mutable.SortedSet[Int]()

    // Submit tasks to executor
    (1 to concurrentThreads).foreach { _ =>
      val future = executor.submit(new Callable[List[Long]] {
        override def call(): List[Long] = {
          val executionTimes = ListBuffer[Long]()
          while (System.currentTimeMillis() - startTime < benchmarkTimeSeconds * 1000) {
            val start = System.currentTimeMillis()
            val resultSize = runTask()
            val end = System.currentTimeMillis()
            numResults.add(resultSize)
            executionTimes += (end - start)
          }
          executionTimes.toList
        }
      })
      futures += future
    }

    // Shutdown the executor after the benchmark time has elapsed
    executor.shutdown()
    try {
      executor.awaitTermination(benchmarkTimeSeconds + 5, TimeUnit.SECONDS)
    } catch {
      case e: InterruptedException => e.printStackTrace()
    }

    // Collect and process results
    val allExecutionTimes = futures.flatMap(_.get()).sorted
    val totalCalls = allExecutionTimes.length
    println(s"Total tasks executed: $totalCalls")
    println(s"Results Sizes are: ${numResults.mkString(",")}")
    val percentiles = printPercentiles(allExecutionTimes.toSeq)
    SingleExperimentResult(
      overallQueryTime = benchmarkTimeSeconds,
      overallWriteTimeTaken = 0L,
      totalCalls,
      percentiles,
      writeLatencies = Seq.empty
    )
  }

  def printPercentiles(executionTimes: Seq[Long]): Seq[PercentileLatency] = {
    val percentiles = List(5, 10, 25, 50, 75, 90, 95, 99)
    percentiles.map { p =>
      val index = (p / 100.0 * executionTimes.length).toInt
      println(s"$p% percentile execution time: ${executionTimes(index)} milliseconds")
      PercentileLatency(p, executionTimes(index))
    }
  }

}

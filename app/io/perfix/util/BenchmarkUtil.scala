package io.perfix.util

import io.perfix.model.{ExperimentResult, PercentileLatency}

import java.util.concurrent.{Callable, Executors, Future, TimeUnit}
import scala.collection.mutable.ListBuffer

object BenchmarkUtil {

  def runBenchmark(concurrentThreads: Int, benchmarkTimeSeconds: Long, runTask: () => Unit): ExperimentResult = {
    val executor = Executors.newFixedThreadPool(concurrentThreads)
    val futures = new ListBuffer[Future[List[Long]]]
    val startTime = System.currentTimeMillis()

    // Submit tasks to executor
    (1 to concurrentThreads).foreach { _ =>
      val future = executor.submit(new Callable[List[Long]] {
        override def call(): List[Long] = {
          val executionTimes = ListBuffer[Long]()
          while (System.currentTimeMillis() - startTime < benchmarkTimeSeconds * 1000) {
            val start = System.currentTimeMillis()
            runTask()
            val end = System.currentTimeMillis()
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
    val percentiles = printPercentiles(allExecutionTimes.toSeq)
    ExperimentResult(benchmarkTimeSeconds, totalCalls, percentiles)
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

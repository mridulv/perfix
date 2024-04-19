package io.perfix.util

import io.perfix.model.{PercentileLatency, ExperimentResult}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.Json

import scala.concurrent.duration._

class BenchmarkUtilSpec extends AnyFlatSpec with Matchers {

  "runBenchmark" should "correctly execute tasks in multiple threads" in {
    val task = () => {
      Thread.sleep(5)
      1
    }
    val concurrentThreads = 5
    val benchmarkTimeSeconds = 1

    val result = BenchmarkUtil.runBenchmark(concurrentThreads, benchmarkTimeSeconds, task)

    result.numberOfCalls should be > 0
    result.overallQueryTime shouldBe benchmarkTimeSeconds
    // Since the task returns a fixed result size, the numResults should contain only that value
    result.queryLatencies.map(_.latency).min should be >= 5.0
    result.writeLatencies shouldBe empty
  }

  it should "calculate percentiles correctly" in {
    val executionTimes = (0 to 99).map(_ * 100L).toList
    val expectedPercentiles = Seq(
      PercentileLatency(5, 500.0),
      PercentileLatency(10, 1000.0),
      PercentileLatency(25, 2500.0),
      PercentileLatency(50, 5000.0),
      PercentileLatency(75, 7500.0),
      PercentileLatency(90, 9000.0),
      PercentileLatency(95, 9500.0),
      PercentileLatency(99, 9900.0)
    )

    val percentiles = BenchmarkUtil.printPercentiles(executionTimes)

    percentiles should contain theSameElementsAs expectedPercentiles
  }

  it should "terminate after the specified benchmark time" in {
    val task = () => 1
    val benchmarkTimeSeconds = 2
    val startTime = System.nanoTime()

    BenchmarkUtil.runBenchmark(1, benchmarkTimeSeconds, task)

    val endTime = System.nanoTime()
    val duration = Duration.fromNanos(endTime - startTime)

    duration should be >= benchmarkTimeSeconds.seconds
    duration should be < (benchmarkTimeSeconds + 5).seconds
  }

  it should "serialize and deserialize PerfixExperimentResult correctly" in {
    val queryLatencies = Seq(PercentileLatency(50, 100.0), PercentileLatency(90, 200.0))
    val writeLatencies = Seq(PercentileLatency(50, 150.0), PercentileLatency(90, 250.0))
    val result = ExperimentResult(
      overallQueryTime = 10L,
      overallWriteTimeTaken = 5L,
      numberOfCalls = 20,
      queryLatencies = queryLatencies,
      writeLatencies = writeLatencies
    )

    val json = Json.toJson(result)
    val deserializedResult = json.validate[ExperimentResult].get

    deserializedResult shouldEqual result
  }

  it should "serialize and deserialize PercentileLatency correctly" in {
    val percentileLatency = PercentileLatency(75, 123.4)

    val json = Json.toJson(percentileLatency)
    val deserializedPercentileLatency = json.validate[PercentileLatency].get

    deserializedPercentileLatency shouldEqual percentileLatency
  }
}

package io.perfix.experiment

import io.perfix.BenchmarkUtil
import io.perfix.context.QuestionExecutionContext
import io.perfix.model.ExperimentParams
import io.perfix.query.PerfixQuery
import io.perfix.question.Questionnaire
import io.perfix.stores.DataStore

import java.util.concurrent.{Callable, Executors, Future, TimeUnit}
import scala.collection.mutable.ListBuffer
import scala.concurrent.duration.DurationInt

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
    BenchmarkUtil.runBenchmark(
      concurrentThreads = experimentParams.concurrentQueries,
      benchmarkTimeSeconds = 60000,
      runTask = () => {
        val query = dataStore.convertQuery(PerfixQuery())
        dataStore.putData()
        // Using experimentParams.concurrentQueriesOpt run experiment those many number of times using query
      }
    )
  }
}

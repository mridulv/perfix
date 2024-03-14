package io.perfix.question.experiment

import ExperimentParamsQuestion.{CONCURRENT_QUERIES, BENCHMARK_TIME_IN_SECONDS, WRITE_BATCH_SIZE}
import io.perfix.exceptions.ParamsAlreadyDefinedException
import io.perfix.model._
import io.perfix.question.Question.QuestionLabel
import io.perfix.question.{Question, QuestionParams}

class ExperimentParamsQuestion(experimentParams: ExperimentParams) extends Question {

  override val mapping: Map[QuestionLabel, QuestionType] = Map(
    CONCURRENT_QUERIES -> QuestionType(DoubleType),
    WRITE_BATCH_SIZE -> QuestionType(IntType, isRequired = false),
    BENCHMARK_TIME_IN_SECONDS -> QuestionType(IntType, isRequired = false)
  )

  override val storeQuestionParams: QuestionParams = experimentParams

  override def shouldAsk(): Boolean = {
    experimentParams.concurrentQueriesOpt.isEmpty
  }

  override def setAnswers(answers: Map[QuestionLabel, Any]): Unit = {
    import experimentParams._
    concurrentQueriesOpt match {
      case Some(_) => throw ParamsAlreadyDefinedException("DataDescription")
      case None =>
        concurrentQueriesOpt = Some(answers(CONCURRENT_QUERIES).toString.toInt)
        writeBatchSize = answers.getOrElse(WRITE_BATCH_SIZE, 100).toString.toInt
        benchmarkTimeSeconds = answers.getOrElse(BENCHMARK_TIME_IN_SECONDS, 15).toString.toInt
    }
  }

}

object ExperimentParamsQuestion {
  val CONCURRENT_QUERIES = "num_queries"
  val WRITE_BATCH_SIZE ="write_batch_size"
  val BENCHMARK_TIME_IN_SECONDS = "benchmark_time_seconds"
}
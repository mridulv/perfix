package io.perfix.question.experiment

import ExperimentParamsForm.{BENCHMARK_TIME_IN_SECONDS, CONCURRENT_QUERIES, PERFIX_QUERY, WRITE_BATCH_SIZE}
import io.perfix.exceptions.ParamsAlreadyDefinedException
import io.perfix.model._
import io.perfix.query.PerfixQuery
import io.perfix.question.Form.FormInputName
import io.perfix.question.{Form, FormParams}
import play.api.libs.json.Json

class ExperimentParamsForm(experimentParams: ExperimentParams) extends Form {

  override val mapping: Map[FormInputName, FormInputType] = Map(
    CONCURRENT_QUERIES -> FormInputType(DoubleType),
    WRITE_BATCH_SIZE -> FormInputType(IntType, isRequired = false),
    BENCHMARK_TIME_IN_SECONDS -> FormInputType(IntType, isRequired = false),
    PERFIX_QUERY -> FormInputType(StringType)
  )

  override val formParams: FormParams = experimentParams

  override def shouldAsk(): Boolean = {
    experimentParams.concurrentQueriesOpt.isEmpty
  }

  override def setAnswers(answers: Map[FormInputName, Any]): Unit = {
    import experimentParams._
    concurrentQueriesOpt match {
      case Some(_) => throw ParamsAlreadyDefinedException("DataDescription")
      case None =>
        concurrentQueriesOpt = Some(answers(CONCURRENT_QUERIES).toString.toInt)
        writeBatchSize = answers.getOrElse(WRITE_BATCH_SIZE, 100).toString.toInt
        benchmarkTimeSeconds = answers.getOrElse(BENCHMARK_TIME_IN_SECONDS, 15).toString.toInt
        perfixQuery = Json.parse(answers(PERFIX_QUERY).toString).as[PerfixQuery]
    }
  }

}

object ExperimentParamsForm {
  val CONCURRENT_QUERIES = "num_queries"
  val WRITE_BATCH_SIZE ="write_batch_size"
  val BENCHMARK_TIME_IN_SECONDS = "benchmark_time_seconds"
  val PERFIX_QUERY = "query"
}
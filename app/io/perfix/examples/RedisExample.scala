package io.perfix.examples

import io.perfix.common.PerfixExperimentExecutor
import io.perfix.question.experiment.DataQuestions._
import io.perfix.question.experiment.ExperimentParamsQuestion.CONCURRENT_QUERIES
import io.perfix.question.redis.RedisConnectionParametersQuestion.{PORT, URL}
import io.perfix.question.redis.RedisTableParamsQuestion.KEY_COLUMN

object RedisExample {

  def main(args: Array[String]): Unit = {
    val mappedVariables: Map[String, Any] = Map(
      ROWS -> 100,
      COLUMNS -> "student_name,student_address",
      CONCURRENT_QUERIES -> 10,
      URL -> "localhost",
      PORT -> 6379,
      KEY_COLUMN -> "student_name"
    )
    val experimentExecutor = new PerfixExperimentExecutor("redis")
    while (experimentExecutor.getQuestionnaireExecutor.hasNext) {
      val question = experimentExecutor.getQuestionnaireExecutor.next()
      val answerMapping = question.map { case (k, v) =>
        k -> mappedVariables(k)
      }
      experimentExecutor.getQuestionnaireExecutor.submit(answerMapping)
    }

    experimentExecutor.runExperiment()
    experimentExecutor.cleanUp()
  }
}

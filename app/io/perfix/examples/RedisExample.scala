package io.perfix.examples

import io.perfix.common.PerfixExperimentExecutor
import io.perfix.question.Question
import io.perfix.question.experiment.DataQuestions._
import io.perfix.question.experiment.ExperimentParamsQuestion.CONCURRENT_QUERIES
import io.perfix.question.redis.RedisConnectionParametersQuestion.{PORT, URL}
import io.perfix.question.redis.RedisTableParamsQuestion.KEY_COLUMN

object RedisExample {

  def main(args: Array[String]): Unit = {
    val mappedVariables: Map[String, Any] = Map(
      ROWS -> 100,
      COLUMNS -> "[{\"columnName\":\"student_name\",\"columnType\":{\"constraint\":{\"startEpoch\":1,\"endEpoch\":2},\"type\":\"EpochType\"}},{\"columnName\":\"student_address\",\"columnType\":{\"type\":\"EpochType\"}}]",
      CONCURRENT_QUERIES -> 10,
      URL -> "localhost",
      PORT -> 6379,
      KEY_COLUMN -> "student_name"
    )
    val experimentExecutor = new PerfixExperimentExecutor("redis")
    while (experimentExecutor.getQuestionnaireExecutor.hasNext) {
      val question = experimentExecutor.getQuestionnaireExecutor.next()
      val answerMapping = question.map { case (k, questionType) =>
        val mappedValue = if (questionType.isRequired) {
          Some(mappedVariables(k))
        } else {
          mappedVariables.get(k)
        }
        k -> mappedValue
      }
      experimentExecutor.getQuestionnaireExecutor.submit(Question.filteredAnswers(answerMapping))
    }

    experimentExecutor.runExperiment()
    experimentExecutor.cleanUp()
  }
}

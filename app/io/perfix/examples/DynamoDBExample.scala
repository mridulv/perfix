package io.perfix.examples

import io.perfix.common.PerfixExperimentExecutor
import io.perfix.question.dynamodb.DynamoDBConnectionParametersQuestions.{ACCESS_ID, ACCESS_SECRET}
import io.perfix.question.dynamodb.DynamoDBTableParamsQuestions._
import io.perfix.question.experiment.DataQuestions._
import io.perfix.question.experiment.ExperimentParamsQuestion.CONCURRENT_QUERIES

object DynamoDBExample {

  def main(args: Array[String]): Unit = {
    val mappedVariables: Map[String, Any] = Map(
      ROWS -> 100000,
      COLUMNS -> "student_name,student_address",
      CONCURRENT_QUERIES -> 10,
      TABLE_NAME -> "test",
      PARTITION_KEY -> "student_name",
      SORT_KEY -> "student_address",
      CONNECTION_URL -> "http://localhost:8000",
      ACCESS_ID -> "**********",
      ACCESS_SECRET -> "**********"
    )
    val experimentExecutor = new PerfixExperimentExecutor("dynamodb")
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

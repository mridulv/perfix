package io.perfix.examples

import io.perfix.common.PerfixExperimentExecutor
import io.perfix.question.AWSCloudParamsQuestion.{AWS_ACCESS_KEY, AWS_ACCESS_SECRET}
import io.perfix.question.Question
import io.perfix.question.dynamodb.DynamoDBGSIParamsQuestions.GSI
import io.perfix.question.dynamodb.DynamoDBTableParamsQuestions._
import io.perfix.question.experiment.DataQuestions._
import io.perfix.question.experiment.ExperimentParamsQuestion.CONCURRENT_QUERIES

object DynamoDBStoreTest {

  def main(args: Array[String]): Unit = {
    val mappedVariables: Map[String, Any] = Map(
      ROWS -> 10000,
      COLUMNS -> "[{\"columnName\":\"student_name\",\"columnType\":{\"type\":\"NameType\",\"isUnique\":true}},{\"columnName\":\"student_address\",\"columnType\":{\"type\":\"AddressType\",\"isUnique\":false}}]",
      CONCURRENT_QUERIES -> 10,
      TABLE_NAME -> "testnw2323",
      PARTITION_KEY -> "student_name",
      SORT_KEY -> "student_address",
      CONNECTION_URL -> "http://localhost:8000",
      AWS_ACCESS_KEY -> "id",
      AWS_ACCESS_SECRET -> "secret",
      GSI -> "{\"gsiParams\":[{\"partitionKey\":\"student_address\",\"sortKey\":\"student_name\"}]}"
    )
    val experimentExecutor = new PerfixExperimentExecutor("dynamodb")
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

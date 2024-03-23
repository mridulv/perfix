package io.perfix.examples

import io.perfix.common.PerfixExperimentExecutor
import io.perfix.question.AWSCloudParamsQuestion.{AWS_ACCESS_KEY, AWS_ACCESS_SECRET, LAUNCH_DB}
import io.perfix.question.Question
import io.perfix.question.documentdb.DocumentDBConnectionParamsQuestion._
import io.perfix.question.documentdb.DocumentDBIndicesParamsQuestion.INDICES_COLUMNS
import io.perfix.question.documentdb.DocumentDBLaunchQuestion.{DB_CLUSTER_IDENTIFIER, INSTANCE_CLASS, MASTER_PASSWORD, MASTER_USERNAME}
import io.perfix.question.documentdb.DocumentDBTableParamsQuestions.COLLECTION_NAME
import io.perfix.question.experiment.DataQuestions._
import io.perfix.question.experiment.ExperimentParamsQuestion.{CONCURRENT_QUERIES, PERFIX_QUERY}

object MongoDBExample {

  def main(args: Array[String]): Unit = {
    val mappedVariables: Map[String, Any] = Map(
      ROWS -> 10000,
      COLUMNS -> "[{\"columnName\":\"student_name\",\"columnType\":{\"type\":\"NameType\",\"isUnique\":true},\"columnValueDistribution\":{\"value\":\"John\",\"probability\":0.1}},{\"columnName\":\"student_address\",\"columnType\":{\"type\":\"AddressType\",\"isUnique\":false}}]",
      CONCURRENT_QUERIES -> 10,
      PERFIX_QUERY -> "{\"filtersOpt\":[{\"field\":\"student_name\",\"fieldValue\":\"John\"}],\"projectedFieldsOpt\":[\"student_address\"],\"limitOpt\":1}",
      DATABASE -> "test",
      URL -> "mongodb://localhost:27017",
      COLLECTION_NAME -> "students",
      INDICES_COLUMNS -> "{\"columns\":[\"student_name\"]}",
      AWS_ACCESS_KEY -> "****************************",
      AWS_ACCESS_SECRET -> "******************************************",
      DB_CLUSTER_IDENTIFIER -> "testing3",
      MASTER_USERNAME -> "root",
      MASTER_PASSWORD -> "********************",
      INSTANCE_CLASS -> "db.r5.large",
      LAUNCH_DB -> false
    )
    val experimentExecutor = new PerfixExperimentExecutor("mongodb")
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

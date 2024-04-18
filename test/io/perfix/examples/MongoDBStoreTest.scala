package io.perfix.examples

import io.perfix.common.ExperimentExecutor
import io.perfix.question.AWSCloudParamsForm.{AWS_ACCESS_KEY, AWS_ACCESS_SECRET, LAUNCH_DB}
import io.perfix.question.Form
import io.perfix.question.documentdb.DocumentDBConnectionParamsForm._
import io.perfix.question.documentdb.DocumentDBIndicesParamsForm.INDICES_COLUMNS
import io.perfix.question.documentdb.DocumentDBLaunchForm.{DB_CLUSTER_IDENTIFIER, INSTANCE_CLASS, MASTER_PASSWORD, MASTER_USERNAME}
import io.perfix.question.documentdb.DocumentDBTableParamsQuestions.COLLECTION_NAME
import io.perfix.question.experiment.DataQuestions._
import io.perfix.question.experiment.ExperimentParamsForm.{CONCURRENT_QUERIES, PERFIX_QUERY}

object MongoDBStoreTest {

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
    val experimentExecutor = new ExperimentExecutor("mongodb")
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
      experimentExecutor.getQuestionnaireExecutor.submit(Form.filteredAnswers(answerMapping))
    }

    experimentExecutor.runExperiment()
    experimentExecutor.cleanUp()
  }
}

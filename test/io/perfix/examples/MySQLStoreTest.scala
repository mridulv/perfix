package io.perfix.examples

import io.perfix.common.ExperimentExecutor
import io.perfix.question.AWSCloudParamsForm._
import io.perfix.question.Form
import io.perfix.question.experiment.DataConfigurationForm._
import io.perfix.question.experiment.ExperimentParamsForm.{CONCURRENT_QUERIES, PERFIX_QUERY}
import io.perfix.question.mysql.ConnectionParamsForm._
import io.perfix.question.mysql.MySQLLaunchForm._
import io.perfix.question.mysql.TableIndicesDetailForm.SECONDARY_INDEX_COLUMNS
import io.perfix.question.mysql.TableParamsQuestions.{DBNAME, TABLENAME}


object MySQLStoreTest {

  def main(args: Array[String]): Unit = {
    val mappedVariables: Map[String, Any] = Map(
      ROWS -> 1000,
      COLUMNS -> "[{\"columnName\":\"student_name\",\"columnType\":{\"type\":\"NameType\",\"isUnique\":true},\"columnValueDistribution\":{\"value\":\"John\",\"probability\":0.1}},{\"columnName\":\"student_address\",\"columnType\":{\"type\":\"AddressType\",\"isUnique\":false}}]",
      CONCURRENT_QUERIES -> 10,
      PERFIX_QUERY -> "{\"filtersOpt\":[{\"field\":\"student_name\",\"fieldValue\":\"John\"}],\"projectedFieldsOpt\":[\"student_address\"],\"limitOpt\":100}",
      USERNAME -> "root",
      URL -> "jdbc:mysql://localhost:3306/perfix",
      PASSWORD -> "test12345",
      DBNAME -> "perfix",
      TABLENAME -> "test",
      SECONDARY_INDEX_COLUMNS -> "student_name",
      INSTANCE_IDENTIFIER -> "dbinstance",
      INSTANCE_TYPE -> "db.t4g.micro",
      AWS_ACCESS_KEY -> "************",
      AWS_ACCESS_SECRET -> "************************************",
      LAUNCH_DB -> false
    )
    val experimentExecutor = new ExperimentExecutor("mysql")
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

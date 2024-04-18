package io.perfix.examples

import io.perfix.common.ExperimentExecutor
import io.perfix.forms.AWSCloudParamsForm._
import io.perfix.forms.Form
import io.perfix.forms.experiment.DataConfigurationForm._
import io.perfix.forms.experiment.ExperimentParamsForm.{CONCURRENT_QUERIES, PERFIX_QUERY}
import io.perfix.forms.mysql.MySQLConnectionParamsForm._
import io.perfix.forms.mysql.MySQLLaunchForm._
import io.perfix.forms.mysql.MySQLTableIndicesDetailForm.SECONDARY_INDEX_COLUMNS
import io.perfix.forms.mysql.MySQLTableParamsForm.{DBNAME, TABLENAME}


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
    while (experimentExecutor.getFormSeriesEvaluator.hasNext) {
      val form = experimentExecutor.getFormSeriesEvaluator.next()
      val answerMapping = form.map { case (k, formInputType) =>
        val mappedValue = if (formInputType.isRequired) {
          Some(mappedVariables(k))
        } else {
          mappedVariables.get(k)
        }
        k -> mappedValue
      }
      experimentExecutor.getFormSeriesEvaluator.submit(Form.filteredAnswers(answerMapping))
    }

    experimentExecutor.runExperiment()
    experimentExecutor.cleanUp()
  }
}

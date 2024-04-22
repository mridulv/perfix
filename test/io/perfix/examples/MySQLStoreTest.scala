package io.perfix.examples

import io.perfix.common.ExperimentExecutor
import io.perfix.forms.AWSCloudParamsForm._
import io.perfix.forms.Form
import io.perfix.forms.mysql.MySQLConnectionParamsForm._
import io.perfix.forms.mysql.MySQLLaunchForm._
import io.perfix.forms.mysql.MySQLTableIndicesDetailForm.SECONDARY_INDEX_COLUMNS
import io.perfix.forms.mysql.MySQLTableParamsForm.{DBNAME, TABLENAME}
import io.perfix.model.{Dataset, ExperimentParams}


object MySQLStoreTest {

  def main(args: Array[String]): Unit = {
    val mappedVariables: Map[String, Any] = Map(
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
    val experimentParams: ExperimentParams = ExperimentParams.experimentParamsForTesting
    val experimentExecutor = new ExperimentExecutor("mysql", experimentParams, dataset = Dataset.datasetForTesting)
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

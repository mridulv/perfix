package io.perfix.examples

import io.perfix.common.ExperimentExecutor
import io.perfix.forms.AWSCloudParamsForm.{AWS_ACCESS_KEY, AWS_ACCESS_SECRET, LAUNCH_DB}
import io.perfix.forms.Form
import io.perfix.forms.documentdb.DocumentDBConnectionParamsForm._
import io.perfix.forms.documentdb.DocumentDBIndicesParamsForm.INDICES_COLUMNS
import io.perfix.forms.documentdb.DocumentDBLaunchForm.{DB_CLUSTER_IDENTIFIER, INSTANCE_CLASS, MASTER_PASSWORD, MASTER_USERNAME}
import io.perfix.forms.documentdb.DocumentDBTableParamsForm.COLLECTION_NAME
import io.perfix.model.{Dataset, ExperimentParams}

object MongoDBStoreTest {

  def main(args: Array[String]): Unit = {
    val mappedVariables: Map[String, Any] = Map(
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
    val experimentParams: ExperimentParams = ExperimentParams.experimentParamsForTesting
    val experimentExecutor = new ExperimentExecutor("mongodb", experimentParams, dataset = Dataset.datasetForTesting)
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

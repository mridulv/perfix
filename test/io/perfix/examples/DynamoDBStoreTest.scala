package io.perfix.examples

import io.perfix.common.ExperimentExecutor
import io.perfix.forms.Form
import io.perfix.forms.dynamodb.DynamoDBGSIParamsForm.GSI
import io.perfix.forms.dynamodb.DynamoDBTableParamsForm._
import io.perfix.model.{Dataset}
import io.perfix.model.experiment.ExperimentParams

object DynamoDBStoreTest {

  def main(args: Array[String]): Unit = {
//    val mappedVariables: Map[String, Any] = Map(
//      TABLE_NAME -> "testnw2323",
//      PARTITION_KEY -> "student_name",
//      SORT_KEY -> "student_address",
//      CONNECTION_URL -> "http://localhost:8000",
//      AWS_ACCESS_KEY -> "id",
//      AWS_ACCESS_SECRET -> "secret",
//      GSI -> "{\"gsiParams\":[{\"partitionKey\":\"student_address\",\"sortKey\":\"student_name\"}]}"
//    )
//    val experimentParams: ExperimentParams = ExperimentParams.experimentParamsForTesting
//    val experimentExecutor = new ExperimentExecutor("dynamodb", experimentParams, dataset = Dataset.datasetForTesting)
//    while (experimentExecutor.getFormSeriesEvaluator.hasNext) {
//      val form = experimentExecutor.getFormSeriesEvaluator.next()
//      val answerMapping = form.map { case (k, formInputType) =>
//        val mappedValue = if (formInputType.isRequired) {
//          Some(mappedVariables(k))
//        } else {
//          mappedVariables.get(k)
//        }
//        k -> mappedValue
//      }
//      experimentExecutor.getFormSeriesEvaluator.submit(Form.filteredAnswers(answerMapping))
//    }
//
//    experimentExecutor.runExperiment()
//    experimentExecutor.cleanUp()
  }
}

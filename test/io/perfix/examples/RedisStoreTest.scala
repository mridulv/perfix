package io.perfix.examples

import io.perfix.common.ExperimentExecutor
import io.perfix.forms.AWSCloudParamsForm.{AWS_ACCESS_KEY, AWS_ACCESS_SECRET}
import io.perfix.forms.Form
import io.perfix.forms.redis.RedisLaunchForm.{CACHE_NODE_TYPE, CLUSTER_ID}
import io.perfix.forms.redis.RedisConnectionParametersForm.{PORT, URL}
import io.perfix.forms.redis.RedisTableParamsForm.KEY_COLUMN
import io.perfix.model.{Dataset, ExperimentParams}

object RedisStoreTest {

  def main(args: Array[String]): Unit = {
    val mappedVariables: Map[String, Any] = Map(
      URL -> "localhost",
      PORT -> 6379,
      KEY_COLUMN -> "student_name",
      CLUSTER_ID -> "newrediscluster",
      CACHE_NODE_TYPE -> "cache.t2.micro",
      AWS_ACCESS_KEY -> "****************************",
      AWS_ACCESS_SECRET -> "******************************************"
    )
    val experimentParams: ExperimentParams = ExperimentParams.experimentParamsForTesting
    val experimentExecutor = new ExperimentExecutor("redis", experimentParams, dataset = Dataset.datasetForTesting)
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

package io.perfix.examples

import io.perfix.common.ExperimentExecutor
import io.perfix.forms.AWSCloudParamsForm.{AWS_ACCESS_KEY, AWS_ACCESS_SECRET}
import io.perfix.forms.Form
import io.perfix.forms.experiment.DataConfigurationForm._
import io.perfix.forms.experiment.ExperimentParamsForm.CONCURRENT_QUERIES
import io.perfix.forms.redis.RedisLaunchForm.{CACHE_NODE_TYPE, CLUSTER_ID}
import io.perfix.forms.redis.RedisConnectionParametersForm.{PORT, URL}
import io.perfix.forms.redis.RedisTableParamsForm.KEY_COLUMN

object RedisStoreTest {

  def main(args: Array[String]): Unit = {
    val mappedVariables: Map[String, Any] = Map(
      ROWS -> 10000,
      COLUMNS -> "[{\"columnName\":\"student_name\",\"columnType\":{\"type\":\"NameType\",\"isUnique\":true}},{\"columnName\":\"student_address\",\"columnType\":{\"type\":\"AddressType\",\"isUnique\":false}}]",
      CONCURRENT_QUERIES -> 10,
      URL -> "localhost",
      PORT -> 6379,
      KEY_COLUMN -> "student_name",
      CLUSTER_ID -> "newrediscluster",
      CACHE_NODE_TYPE -> "cache.t2.micro",
      AWS_ACCESS_KEY -> "****************************",
      AWS_ACCESS_SECRET -> "******************************************"
    )
    val experimentExecutor = new ExperimentExecutor("redis")
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

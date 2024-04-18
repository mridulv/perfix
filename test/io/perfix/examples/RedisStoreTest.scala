package io.perfix.examples

import io.perfix.common.ExperimentExecutor
import io.perfix.question.AWSCloudParamsForm.{AWS_ACCESS_KEY, AWS_ACCESS_SECRET}
import io.perfix.question.Form
import io.perfix.question.experiment.DataQuestions._
import io.perfix.question.experiment.ExperimentParamsForm.CONCURRENT_QUERIES
import io.perfix.question.redis.ElastiCacheLaunchQuestion.{CACHE_NODE_TYPE, CLUSTER_ID}
import io.perfix.question.redis.RedisConnectionParametersForm.{PORT, URL}
import io.perfix.question.redis.RedisTableParamsForm.KEY_COLUMN

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

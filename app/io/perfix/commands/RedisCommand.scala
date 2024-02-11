package io.perfix.commands

import io.perfix.common.PerfixExperimentExecutor
import io.perfix.question.AWSCloudParamsQuestion.{AWS_ACCESS_KEY, AWS_ACCESS_SECRET}
import io.perfix.question.Question
import io.perfix.question.experiment.DataQuestions.{COLUMNS, ROWS}
import io.perfix.question.experiment.ExperimentParamsQuestion.CONCURRENT_QUERIES
import io.perfix.question.redis.ElastiCacheLaunchQuestion.{CACHE_NODE_TYPE, CLUSTER_ID}
import io.perfix.question.redis.RedisConnectionParametersQuestion.{PORT, URL}
import io.perfix.question.redis.RedisTableParamsQuestion.KEY_COLUMN
import picocli.CommandLine

@CommandLine.Command(name = "redis", description = Array("Run performance experiment on Redis"))
class RedisCommand extends Runnable {
  @CommandLine.Option(names = Array("-r", "--rows"), description = Array("Number of rows"))
  var rows: Int = 10000

  @CommandLine.Option(names = Array("-c", "--columns"), description = Array("Columns configuration"))
  var columns: String = "[{\"columnName\":\"student_name\",\"columnType\":{\"type\":\"NameType\",\"isUnique\":true}},{\"columnName\":\"student_address\",\"columnType\":{\"type\":\"AddressType\",\"isUnique\":false}}]"

  @CommandLine.Option(names = Array("-q", "--concurrent-queries"), description = Array("Concurrent queries"))
  var concurrentQueries: Int = 10

  @CommandLine.Option(names = Array("-u", "--url"), description = Array("Redis URL"))
  var url: String = "localhost"

  @CommandLine.Option(names = Array("-p", "--port"), description = Array("Redis port"))
  var port: Int = 6379

  @CommandLine.Option(names = Array("-k", "--key-column"), description = Array("Key column"))
  var keyColumn: String = "student_name"

  @CommandLine.Option(names = Array("-i", "--cluster-id"), description = Array("Cluster ID"))
  var clusterId: String = "newrediscluster"

  @CommandLine.Option(names = Array("-n", "--cache-node-type"), description = Array("Cache node type"))
  var cacheNodeType: String = "cache.t2.micro"

  @CommandLine.Option(names = Array("-a", "--aws-access-key"), description = Array("AWS access key"))
  var awsAccessKey: String = "****************************"

  @CommandLine.Option(names = Array("-s", "--aws-access-secret"), description = Array("AWS access secret"))
  var awsAccessSecret: String = "******************************************"

  override def run(): Unit = {
    println(s"Running performance experiment on Redis with $rows rows and $concurrentQueries concurrent queries")

    val mappedVariables: Map[String, Any] = Map(
      ROWS -> rows,
      COLUMNS -> columns,
      CONCURRENT_QUERIES -> concurrentQueries,
      URL -> url,
      PORT -> port,
      KEY_COLUMN -> keyColumn,
      CLUSTER_ID -> clusterId,
      CACHE_NODE_TYPE -> cacheNodeType,
      AWS_ACCESS_KEY -> awsAccessKey,
      AWS_ACCESS_SECRET -> awsAccessSecret
    )

    val experimentExecutor = new PerfixExperimentExecutor("redis")
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


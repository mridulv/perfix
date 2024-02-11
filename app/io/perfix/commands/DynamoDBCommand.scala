package io.perfix.commands

import io.perfix.common.PerfixExperimentExecutor
import io.perfix.question.AWSCloudParamsQuestion.{AWS_ACCESS_KEY, AWS_ACCESS_SECRET}
import io.perfix.question.Question
import io.perfix.question.dynamodb.DynamoDBGSIParamsQuestions.GSI
import io.perfix.question.dynamodb.DynamoDBTableParamsQuestions.{CONNECTION_URL, PARTITION_KEY, SORT_KEY, TABLE_NAME}
import io.perfix.question.experiment.DataQuestions.{COLUMNS, ROWS}
import io.perfix.question.experiment.ExperimentParamsQuestion.CONCURRENT_QUERIES
import picocli.CommandLine

@CommandLine.Command(name = "dynamodb", description = Array("Run performance experiment on DynamoDB"))
class DynamoDBCommand extends Runnable {
  @CommandLine.Option(names = Array("-r", "--rows"), description = Array("Number of rows"))
  var rows: Int = 10000

  @CommandLine.Option(names = Array("-c", "--columns"), description = Array("Columns configuration"))
  var columns: String = "[{\"columnName\":\"student_name\",\"columnType\":{\"type\":\"NameType\",\"isUnique\":true}},{\"columnName\":\"student_address\",\"columnType\":{\"type\":\"AddressType\",\"isUnique\":false}}]"

  @CommandLine.Option(names = Array("-q", "--concurrent-queries"), description = Array("Concurrent queries"))
  var concurrentQueries: Int = 10

  @CommandLine.Option(names = Array("-t", "--table-name"), description = Array("Table name"))
  var tableName: String = "testnw2323"

  @CommandLine.Option(names = Array("-pk", "--partition-key"), description = Array("Partition key"))
  var partitionKey: String = "student_name"

  @CommandLine.Option(names = Array("-sk", "--sort-key"), description = Array("Sort key"))
  var sortKey: String = "student_address"

  @CommandLine.Option(names = Array("-cu", "--connection-url"), description = Array("Connection URL"))
  var connectionUrl: String = "http://localhost:8000"

  @CommandLine.Option(names = Array("-a", "--aws-access-key"), description = Array("AWS access key"))
  var awsAccessKey: String = "id"

  @CommandLine.Option(names = Array("-s", "--aws-access-secret"), description = Array("AWS access secret"))
  var awsAccessSecret: String = "secret"

  @CommandLine.Option(names = Array("-g", "--gsi"), description = Array("GSI parameters"))
  var gsi: String = "{\"gsiParams\":[{\"partitionKey\":\"student_address\",\"sortKey\":\"student_name\"}]}"

  override def run(): Unit = {
    println(s"Running performance experiment on DynamoDB with $rows rows and $concurrentQueries concurrent queries")

    val mappedVariables: Map[String, Any] = Map(
      ROWS -> rows,
      COLUMNS -> columns,
      CONCURRENT_QUERIES -> concurrentQueries,
      TABLE_NAME -> tableName,
      PARTITION_KEY -> partitionKey,
      SORT_KEY -> sortKey,
      CONNECTION_URL -> connectionUrl,
      AWS_ACCESS_KEY -> awsAccessKey,
      AWS_ACCESS_SECRET -> awsAccessSecret,
      GSI -> gsi
    )

    val experimentExecutor = new PerfixExperimentExecutor("dynamodb")
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

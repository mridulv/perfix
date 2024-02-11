package io.perfix.commands

import io.perfix.common.PerfixExperimentExecutor
import io.perfix.question.AWSCloudParamsQuestion.{AWS_ACCESS_KEY, AWS_ACCESS_SECRET}
import io.perfix.question.Question
import io.perfix.question.documentdb.DocumentDBConnectionParamsQuestion.{DATABASE, URL}
import io.perfix.question.documentdb.DocumentDBIndicesParamsQuestion.INDICES_COLUMNS
import io.perfix.question.documentdb.DocumentDBLaunchQuestion.{DB_CLUSTER_IDENTIFIER, INSTANCE_CLASS, MASTER_PASSWORD, MASTER_USERNAME}
import io.perfix.question.documentdb.DocumentDBTableParamsQuestions.COLLECTION_NAME
import io.perfix.question.experiment.DataQuestions.{COLUMNS, ROWS}
import io.perfix.question.experiment.ExperimentParamsQuestion.CONCURRENT_QUERIES
import picocli.CommandLine

@CommandLine.Command(name = "mongodb", description = Array("Run performance experiment on MongoDB"))
class MongoDBCommand extends Runnable {
  @CommandLine.Option(names = Array("-r", "--rows"), description = Array("Number of rows"))
  var rows: Int = 10000

  @CommandLine.Option(names = Array("-c", "--columns"), description = Array("Columns configuration"))
  var columns: String = "[{\"columnName\":\"student_name\",\"columnType\":{\"type\":\"NameType\",\"isUnique\":true}},{\"columnName\":\"student_address\",\"columnType\":{\"type\":\"AddressType\",\"isUnique\":false}}]"

  @CommandLine.Option(names = Array("-q", "--concurrent-queries"), description = Array("Concurrent queries"))
  var concurrentQueries: Int = 10

  @CommandLine.Option(names = Array("-d", "--database"), description = Array("MongoDB database"))
  var database: String = "test"

  @CommandLine.Option(names = Array("-u", "--url"), description = Array("MongoDB URL"))
  var url: String = "localhost:27017"

  @CommandLine.Option(names = Array("-cn", "--collection-name"), description = Array("Collection name"))
  var collectionName: String = "students"

  @CommandLine.Option(names = Array("-i", "--indices-columns"), description = Array("Indices columns"))
  var indicesColumns: String = "{\"columns\":[\"student_name\"]}"

  @CommandLine.Option(names = Array("-a", "--aws-access-key"), description = Array("AWS access key"))
  var awsAccessKey: String = "****************************"

  @CommandLine.Option(names = Array("-s", "--aws-access-secret"), description = Array("AWS access secret"))
  var awsAccessSecret: String = "******************************************"

  @CommandLine.Option(names = Array("-cid", "--cluster-identifier"), description = Array("Cluster identifier"))
  var clusterIdentifier: String = "testing3"

  @CommandLine.Option(names = Array("-mu", "--master-username"), description = Array("Master username"))
  var masterUsername: String = "root"

  @CommandLine.Option(names = Array("-mp", "--master-password"), description = Array("Master password"))
  var masterPassword: String = "********************"

  @CommandLine.Option(names = Array("-ic", "--instance-class"), description = Array("Instance class"))
  var instanceClass: String = "db.r5.large"

  override def run(): Unit = {
    println(s"Running performance experiment on MongoDB with $rows rows and $concurrentQueries concurrent queries")

    val mappedVariables: Map[String, Any] = Map(
      ROWS -> rows,
      COLUMNS -> columns,
      CONCURRENT_QUERIES -> concurrentQueries,
      DATABASE -> database,
      URL -> url,
      COLLECTION_NAME -> collectionName,
      INDICES_COLUMNS -> indicesColumns,
      AWS_ACCESS_KEY -> awsAccessKey,
      AWS_ACCESS_SECRET -> awsAccessSecret,
      DB_CLUSTER_IDENTIFIER -> clusterIdentifier,
      MASTER_USERNAME -> masterUsername,
      MASTER_PASSWORD -> masterPassword,
      INSTANCE_CLASS -> instanceClass
    )

    val experimentExecutor = new PerfixExperimentExecutor("mongodb")
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

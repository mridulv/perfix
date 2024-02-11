package io.perfix.commands

import io.perfix.common.PerfixExperimentExecutor
import io.perfix.question.AWSCloudParamsQuestion.{AWS_ACCESS_KEY, AWS_ACCESS_SECRET}
import io.perfix.question.Question
import io.perfix.question.experiment.DataQuestions.{COLUMNS, ROWS}
import io.perfix.question.experiment.ExperimentParamsQuestion.CONCURRENT_QUERIES
import io.perfix.question.mysql.ConnectionParamsQuestion.{PASSWORD, URL, USERNAME}
import io.perfix.question.mysql.MySQLLaunchQuestion.{INSTANCE_IDENTIFIER, INSTANCE_TYPE}
import io.perfix.question.mysql.TableIndicesDetailQuestion.{PRIMARY_INDEX_COLUMN, SECONDARY_INDEX_COLUMNS}
import io.perfix.question.mysql.TableParamsQuestions.{DBNAME, TABLENAME}
import picocli.CommandLine

@CommandLine.Command(name = "mysql", mixinStandardHelpOptions = true, description = Array("Run performance experiment on MySQL"))
class MySQLCommand {
  @CommandLine.Option(names = Array("-r", "--rows"), description = Array("Number of rows"))
  var rows: Int = 1000

  @CommandLine.Option(names = Array("-c", "--concurrent-queries"), description = Array("Number of concurrent queries"))
  var concurrentQueries: Int = 10

  @CommandLine.Option(names = Array("-c", "--columns"), description = Array("Columns configuration"))
  var columns: String = "[{\"columnName\":\"student_name\",\"columnType\":{\"type\":\"NameType\",\"isUnique\":true}},{\"columnName\":\"student_address\",\"columnType\":{\"type\":\"AddressType\",\"isUnique\":false}}]"

  @CommandLine.Option(names = Array("-u", "--url"), description = Array("MySQL connection URL"))
  var url: String = "jdbc:mysql://localhost:3306/perfix?autoReconnect=true&useSSL=false&allowPublicKeyRetrieval=true"

  @CommandLine.Option(names = Array("-n", "--username"), description = Array("MySQL username"))
  var username: String = "root"

  @CommandLine.Option(names = Array("-p", "--password"), description = Array("MySQL password"))
  var password: String = "*********"

  @CommandLine.Option(names = Array("-d", "--dbname"), description = Array("MySQL database name"))
  var dbname: String = "perfix"

  @CommandLine.Option(names = Array("-t", "--tablename"), description = Array("MySQL table name"))
  var tablename: String = "test"

  @CommandLine.Option(names = Array("--primary-index-column"), description = Array("Primary index column"))
  var primaryIndexColumn: String = "student_name"

  @CommandLine.Option(names = Array("--secondary-index-columns"), description = Array("Secondary index columns"))
  var secondaryIndexColumns: String = "student_name,student_address"

  @CommandLine.Option(names = Array("--instance-identifier"), description = Array("Instance identifier"))
  var instanceIdentifier: String = "dbinstance"

  @CommandLine.Option(names = Array("--instance-type"), description = Array("Instance type"))
  var instanceType: String = "db.t4g.micro"

  @CommandLine.Option(names = Array("--aws-access-key"), description = Array("AWS access key"))
  var awsAccessKey: String = "************"

  @CommandLine.Option(names = Array("--aws-access-secret"), description = Array("AWS access secret"))
  var awsAccessSecret: String = "************************************"

  @CommandLine.Spec
  private var spec: CommandLine.Model.CommandSpec = _

  @CommandLine.Command(name = "run", description = Array("Run the MySQL performance experiment"))
  def run(): Unit = {
    println(s"Running performance experiment on MySQL with $rows rows and $concurrentQueries concurrent queries")

    val mappedVariables: Map[String, Any] = Map(
      ROWS -> rows,
      COLUMNS -> columns,
      CONCURRENT_QUERIES -> concurrentQueries,
      URL -> url,
      USERNAME -> username,
      PASSWORD -> password,
      DBNAME -> dbname,
      TABLENAME -> tablename,
      PRIMARY_INDEX_COLUMN -> primaryIndexColumn,
      SECONDARY_INDEX_COLUMNS -> secondaryIndexColumns,
      INSTANCE_IDENTIFIER -> instanceIdentifier,
      INSTANCE_TYPE -> instanceType,
      AWS_ACCESS_KEY -> awsAccessKey,
      AWS_ACCESS_SECRET -> awsAccessSecret
    )

    val experimentExecutor = new PerfixExperimentExecutor("mysql")
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
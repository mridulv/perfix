package io.perfix.e2e

import io.perfix.common.PerfixExperimentExecutor
import io.perfix.question.AWSCloudParamsQuestion.{AWS_ACCESS_KEY, AWS_ACCESS_SECRET, LAUNCH_DB}
import io.perfix.question.Question
import io.perfix.question.experiment.DataQuestions.{COLUMNS, ROWS}
import io.perfix.question.experiment.ExperimentParamsQuestion.{BENCHMARK_TIME_IN_SECONDS, CONCURRENT_QUERIES, PERFIX_QUERY}
import io.perfix.question.mysql.ConnectionParamsQuestion.{PASSWORD, URL, USERNAME}
import io.perfix.question.mysql.MySQLLaunchQuestion.{INSTANCE_IDENTIFIER, INSTANCE_TYPE}
import io.perfix.question.mysql.TableParamsQuestions.{DBNAME, TABLENAME}
import org.mockito.MockitoSugar
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.sql.DriverManager

class PerfixExperimentExecutorIT extends AnyFlatSpec with Matchers with MockitoSugar with BeforeAndAfterAll {

  behavior of "PerfixExperimentExecutor"

  private val url = "jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1"
  private val username = "sa"
  private val password = ""

  override def beforeAll: Unit = {
    val connection = DriverManager.getConnection(url, username, password)
    val statement = connection.createStatement()
    statement.execute("CREATE SCHEMA IF NOT EXISTS testdb;")
    statement.close()
  }

  it should "connect and initialize database correctly" in {
    val mappedVariables: Map[String, Any] = Map(
      ROWS -> 1000,
      COLUMNS -> "[{\"columnName\":\"student_name\",\"columnType\":{\"type\":\"NameType\",\"isUnique\":true},\"columnValueDistribution\":{\"value\":\"John\",\"probability\":0.1}},{\"columnName\":\"student_address\",\"columnType\":{\"type\":\"AddressType\",\"isUnique\":false}}]",
      CONCURRENT_QUERIES -> 10,
      BENCHMARK_TIME_IN_SECONDS -> 5,
      PERFIX_QUERY -> "{\"limitOpt\":100}",
      USERNAME -> username,
      URL -> url,
      PASSWORD -> password,
      DBNAME -> "testdb",
      TABLENAME -> "test",
      INSTANCE_IDENTIFIER -> "dbinstance",
      INSTANCE_TYPE -> "*******",
      AWS_ACCESS_KEY -> "************",
      AWS_ACCESS_SECRET -> "**********",
      LAUNCH_DB -> false
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

    val result = experimentExecutor.runExperiment()
    experimentExecutor.cleanUp()
    result.overallQueryTime should be (5)
    result.writeLatencies.length should be (8)
    result.queryLatencies.length should be (8)
  }
}

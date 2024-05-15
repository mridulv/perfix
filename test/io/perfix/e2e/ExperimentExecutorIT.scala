package io.perfix.e2e

import io.perfix.common.ExperimentExecutor
import io.perfix.forms.AWSCloudParamsForm.{AWS_ACCESS_KEY, AWS_ACCESS_SECRET, LAUNCH_DB}
import io.perfix.forms.Form
import io.perfix.forms.mysql.MySQLConnectionParamsForm.{PASSWORD, URL, USERNAME}
import io.perfix.forms.mysql.MySQLLauncher.{INSTANCE_IDENTIFIER, INSTANCE_TYPE}
import io.perfix.forms.mysql.MySQLTableParamsForm.{DBNAME, TABLENAME}
import io.perfix.model.{ColumnDescription, DatabaseConfigId, Dataset, DatasetId, DatasetParams, ExperimentParams, FormInputValue, FormInputValues}
import io.perfix.query.PerfixQuery
import org.mockito.MockitoSugar
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.Json

import java.sql.DriverManager
import scala.util.Random

class ExperimentExecutorIT extends AnyFlatSpec with Matchers with MockitoSugar with BeforeAndAfterAll {

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
    val cols = Json.parse("[{\"columnName\":\"student_name\",\"columnType\":{\"type\":\"NameType\",\"isUnique\":true},\"columnValueDistribution\":{\"value\":\"John\",\"probability\":0.1}},{\"columnName\":\"student_address\",\"columnType\":{\"type\":\"AddressType\",\"isUnique\":false}}]").as[Seq[ColumnDescription]]
    val experimentParams = ExperimentParams(
      None,
      name = s"exp-${Random.nextInt()}",
      concurrentQueries = 10,
      experimentTimeInSeconds = 5,
      query = PerfixQuery(limitOpt = Some(100)),
      datasetId = DatasetId(-1),
      databaseConfigId = DatabaseConfigId(-1),
      experimentResult = None
    )
    val datasetParams = DatasetParams(
      id = None,
      name = s"dataset-${Random.nextInt()}",
      rows = 100,
      columns = cols
    )

    val experimentExecutor = new ExperimentExecutor("mysql", experimentParams, dataset = datasetParams.dataset)
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

    val result = experimentExecutor.runExperiment()
    experimentExecutor.cleanUp()
    result.overallQueryTime should be (5)
    result.writeLatencies.length should be (8)
    result.queryLatencies.length should be (8)
  }
}

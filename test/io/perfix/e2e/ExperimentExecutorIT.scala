package io.perfix.e2e

import io.perfix.experiment.SimplePerformanceExperiment
import io.perfix.model.experiment.{ExperimentParams, ExperimentState, SingleExperimentResult}
import io.perfix.model.store.StoreType
import io.perfix.model._
import io.perfix.model.api.{DatabaseConfigDetails, DatabaseConfigId, DatabaseConfigParams, DatasetDetails, DatasetId, DatasetParams}
import io.perfix.query.SqlDBQueryBuilder
import io.perfix.stores.mysql.{MySQLStore, RDSDatabaseSetupParams}
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

  it should "connect and initialize database correctly" ignore {
    val cols = Json.parse("[{\"columnName\":\"student_name\",\"columnType\":{\"type\":\"NameType\",\"isUnique\":true},\"columnValueDistribution\":{\"value\":\"John\",\"probability\":0.1}},{\"columnName\":\"student_address\",\"columnType\":{\"type\":\"AddressType\",\"isUnique\":false}}]").as[Seq[ColumnDescription]]
    val experimentParams = ExperimentParams(
      None,
      name = s"exp-${Random.nextInt()}",
      concurrentQueries = 10,
      experimentTimeInSeconds = Some(5),
      dbQuery = SqlDBQueryBuilder(tableName = "table"),
      databaseConfigs = Seq(DatabaseConfigDetails(DatabaseConfigId(-1))),
      experimentResults = None,
      createdAt = Some(System.currentTimeMillis()),
      experimentState = Some(ExperimentState.Created)
    )
    val datasetParams = DatasetParams(
      id = None,
      name = s"dataset-${Random.nextInt()}",
      description = "some desc",
      rows = 100,
      columns = Some(cols)
    )
    val mysqlStoreParams = RDSDatabaseSetupParams(
      instanceType = "db.t3.medium",
      tableName = "test",
      primaryIndexColumn = Some("student_name"),
      secondaryIndexesColumn = None
    )
    val databaseConfig = DatabaseConfigParams(
      name = "mysql-config",
      dataStore = StoreType.MySQL,
      datasetDetails = DatasetDetails(DatasetId(-1)),
      databaseSetupParams = mysqlStoreParams
    )
    val experimentExecutor = new SimplePerformanceExperiment(
      new MySQLStore(datasetParams, mysqlStoreParams),
      experimentParams,
      dataset = datasetParams.datasets.datasets.head
    )

    experimentExecutor.init()
    val result = experimentExecutor.run().asInstanceOf[SingleExperimentResult]
    experimentExecutor.cleanup()
    result.overallQueryTime should be (5)
    result.writeLatencies.length should be (8)
    result.queryLatencies.length should be (8)
  }
}

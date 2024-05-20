package io.perfix.e2e

import io.perfix.experiment.SimplePerformanceExperiment
import io.perfix.model.experiment.{ExperimentParams, ExperimentState}
import io.perfix.model.store.{MySQLStoreParams, StoreType}
import io.perfix.model.{ColumnDescription, DatabaseConfigId, DatabaseConfigParams, DatasetId, DatasetParams}
import io.perfix.query.PerfixQuery
import io.perfix.stores.mysql.MySQLStore
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
    val cols = Json.parse("[{\"columnName\":\"student_name\",\"columnType\":{\"type\":\"NameType\",\"isUnique\":true},\"columnValueDistribution\":{\"value\":\"John\",\"probability\":0.1}},{\"columnName\":\"student_address\",\"columnType\":{\"type\":\"AddressType\",\"isUnique\":false}}]").as[Seq[ColumnDescription]]
    val experimentParams = ExperimentParams(
      None,
      name = s"exp-${Random.nextInt()}",
      concurrentQueries = 10,
      experimentTimeInSeconds = 5,
      query = PerfixQuery(limitOpt = Some(100)),
      databaseConfigId = DatabaseConfigId(-1),
      experimentResult = None,
      createdAt = Some(System.currentTimeMillis()),
      experimentState = Some(ExperimentState.Created)
    )
    val datasetParams = DatasetParams(
      id = None,
      name = s"dataset-${Random.nextInt()}",
      description = "some desc",
      rows = 100,
      columns = cols
    )
    val mysqlStoreParams = MySQLStoreParams(
      instanceType = "db.t3.medium",
      tableName = "test",
      primaryIndexColumn = Some("student_name"),
      secondaryIndexesColumn = None
    )
    val databaseConfig = DatabaseConfigParams(
      name = "mysql-config",
      dataStore = StoreType.MySQLStoreType,
      datasetId = DatasetId(-1),
      storeParams = mysqlStoreParams
    )
    val experimentExecutor = new SimplePerformanceExperiment[MySQLStoreParams](
      new MySQLStore(datasetParams, mysqlStoreParams),
      experimentParams,
      dataset = datasetParams.dataset
    )

    experimentExecutor.init()
    val result = experimentExecutor.run()
    experimentExecutor.cleanup()
    result.overallQueryTime should be (5)
    result.writeLatencies.length should be (8)
    result.queryLatencies.length should be (8)
  }
}

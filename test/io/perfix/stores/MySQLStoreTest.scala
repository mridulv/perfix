package io.perfix.stores

import io.perfix.model.api.DatasetParams
import io.perfix.model.{ColumnDescription, NameType}
import io.perfix.query.SqlDBQueryBuilder
import io.perfix.stores.mysql._
import org.mockito.MockitoSugar
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.sql.{Connection, DriverManager}
import scala.util.Random

class MySQLStoreTest extends AnyFlatSpec with Matchers with MockitoSugar with BeforeAndAfterAll {

  behavior of "MySQLStore"

  private var connection: Connection = null
  private var mySQLStore: MySQLStore = null
  private val url = "jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1"
  private val username = "sa"
  private val password = ""

  override def beforeAll() {
    setupDatabase()
  }

  override def afterAll() {
    cleanupDatabase()
  }

  it should "connect and initialize database correctly" in {
    val statement = connection.createStatement()
    val resultSet = statement.executeQuery("SHOW TABLES FROM testdb;")
    resultSet.next() shouldBe true
    resultSet.getString(1) shouldEqual "TEST"
    println(resultSet.getString(1))
    connection.close()
  }

  it should "insert data correctly" in {
    val rows = Seq(Map("name" -> "test22"))
    mySQLStore.putData(rows)
    val res = mySQLStore.readData(SqlDBQueryBuilder(filtersOpt = None, tableName = "TEST"))
    println(res.size)
    res.size should be (1)
  }

  def setupDatabase(): Unit = {
    val datasetParams = DatasetParams(
      id = None,
      name = s"dataset-${Random.nextInt()}",
      description = "desc",
      rows = 100,
      columns = Some(Seq(ColumnDescription("name", NameType())))
    )
    val mysqlStoreParams = RDSDatabaseSetupParams(
      instanceType = "db.t3.medium",
      tableName = "test",
      primaryIndexColumn = None,
      secondaryIndexesColumn = Some(Seq("name")),
      dbDetails = Some(MySQLConnectionParams(url, username, password)),
      dbName = Some("testdb")
    )

    mySQLStore = new MySQLStore(datasetParams, mysqlStoreParams)
    connection = DriverManager.getConnection(url, username, password)
    initializeDatabase(connection)

    mySQLStore.connectAndInitialize()
  }

  def cleanupDatabase(): Unit = {
    connection.close()
  }

  def initializeDatabase(connection: Connection): Unit = {
    val statement = connection.createStatement()
    statement.execute("CREATE SCHEMA IF NOT EXISTS testdb;")
    statement.close()
  }
}

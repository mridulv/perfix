package io.perfix.stores

import io.perfix.model.{ColumnDescription, DataDescription, NameType}
import io.perfix.query.PerfixQuery
import io.perfix.stores.mysql._
import org.mockito.MockitoSugar
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.sql.{Connection, DriverManager}

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
    resultSet.getString(1) shouldEqual "TESTTABLE"
    println(resultSet.getString(1))
    connection.close()
  }

  it should "insert data correctly" in {
    val rows = Seq(Map("name" -> "test22"))
    mySQLStore.putData(rows)
    val res = mySQLStore.readData(PerfixQuery(filtersOpt = None))
    println(res.size)
    res.size should be (1)
  }

  def setupDatabase(): Unit = {
    mySQLStore = new MySQLStore()
    connection = DriverManager.getConnection(url, username, password)
    initializeDatabase(connection)
    val dataDescription = DataDescription()
    dataDescription.rowsOpt = Some(100)
    dataDescription.columnsOpt = Some(
      Seq(
        ColumnDescription("name", NameType())
      )
    )

    val questionnaire = mySQLStore.storeInputs(dataDescription)

    questionnaire.mySQLParams.mySQLConnectionParams = Some(MySQLConnectionParams(url, username, password))
    questionnaire.mySQLParams.mySQLTableParams = Some(MySQLTableParams("testdb", "testTable"))
    questionnaire.mySQLParams.mySQLTableIndexesParams = Some(MySQLTableIndexesParams(None, None))

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

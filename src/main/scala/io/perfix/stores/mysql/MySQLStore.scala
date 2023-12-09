package io.perfix.stores.mysql

import io.perfix.exceptions.InvalidStateException
import io.perfix.model.{ColumnDescription, DataDescription, DataWithDescription}
import io.perfix.model.URLType.toSqlType
import io.perfix.stores.DataStore

import java.sql.{Connection, DriverManager}

class MySQLStore(dataWithDescription: DataWithDescription) extends DataStore {
  private var connection: Connection = _
  private val mySQLParams = MySQLParams(dataWithDescription.dataDescription)
  private val mySQLQuestionnaire = MySQLQuestionnaire(mySQLParams)

  def questions(): MySQLQuestionnaire = {
    mySQLQuestionnaire
  }

  def initialize(): Unit = {
    val connectionParams = mySQLParams.mySQLConnectionParams match {
      case Some(con) => con
      case None => throw InvalidStateException("Connection should have been defined")
    }
    import connectionParams._

    Class.forName("com.mysql.cj.jdbc.Driver").newInstance
    connection = DriverManager.getConnection(url, username, password)
    val statement = connection.createStatement()

    val tableParams = mySQLParams.mySQLTableParams match {
      case Some(tableParams) => tableParams
      case None => throw InvalidStateException("Table Params should have been defined")
    }
    import connectionParams._

    val sql = createTableStatement(tableParams.dbName + "." + tableParams.tableName, dataWithDescription.dataDescription.columns)
    statement.executeUpdate(sql)
    statement.close()
  }

  override def putData(rate: Int): Unit = {
    val data = dataWithDescription.data
    val delay = 1000 / rate
    data.foreach { row =>
      val tableParams = mySQLParams.mySQLTableParams match {
        case Some(tableParams) => tableParams
        case None => throw InvalidStateException("Table Params should have been defined")
      }
      
      val allKeys = row.keys.toSeq
      val columnNames = row.keys.mkString(", ")
      val valuePlaceholders = allKeys.map(_ => "?").mkString(", ")

      val sql = s"INSERT INTO ${tableParams.tableName} ($columnNames) VALUES ($valuePlaceholders);"
      val preparedStatement = connection.prepareStatement(sql)
      for(i <- 1 to allKeys.length) {
        preparedStatement.setObject(i, row(s"${allKeys(i - 1)}"))
      }
      preparedStatement.executeUpdate()
      preparedStatement.close()
      Thread.sleep(delay)
    }
  }

  override def readData(query: String): Seq[Map[String, Any]] = {
    val statement = connection.createStatement()
    val resultSet = statement.executeQuery(query)
    val result = Seq.empty[Map[String, Any]] // Implement conversion
    statement.close()
    result
  }

  private def createTableStatement(tableName: String, columns: Seq[ColumnDescription]): String = {
    val columnDefs = columns.map(col => s"${col.columnName} ${toSqlType(col.columnType)}").mkString(", ")
    s"CREATE TABLE $tableName ($columnDefs);"
  }
}

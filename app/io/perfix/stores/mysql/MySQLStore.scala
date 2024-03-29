package io.perfix.stores.mysql

import io.perfix.exceptions.InvalidStateException
import io.perfix.launch.{AWSCloudParams, LaunchStoreQuestion}
import io.perfix.model.ColumnType.toSqlType
import io.perfix.model.{ColumnDescription, DataDescription}
import io.perfix.stores.DataStore
import io.perfix.query.PerfixQuery
import io.perfix.question.mysql.MySQLLaunchQuestion

import java.sql.{Connection, DriverManager, ResultSet}

class MySQLStore extends DataStore {
  private[stores] var connection: Connection = _
  private var dataDescription: DataDescription = _
  private val mySQLParams: MySQLParams = MySQLParams()

  override def launch(awsCloudParams: AWSCloudParams): Option[LaunchStoreQuestion] = {
    Some(new MySQLLaunchQuestion(awsCloudParams, mySQLParams))
  }

  override def storeInputs(dataDescription: DataDescription): MySQLQuestionnaire = {
    this.dataDescription = dataDescription
    MySQLQuestionnaire(mySQLParams)
  }

  def connectAndInitialize(): Unit = {
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

    val tableIndexesParams = mySQLParams.mySQLTableIndexesParams match {
      case Some(tableIndexesParams) => tableIndexesParams
      case None => throw InvalidStateException("Table Index Params should have been defined")
    }

    val sql = createTableStatement(tableParams.dbName + "." + tableParams.tableName, dataDescription.columns)
    statement.executeUpdate(sql)

    val indexSql = createTableIndexesStatement(tableIndexesParams.primaryIndexColumn, tableIndexesParams.secondaryIndexesColumn)
    if (indexSql.nonEmpty) {
      statement.executeUpdate(indexSql)
    }

    statement.close()
  }

  override def putData(rows: Seq[Map[String, Any]]): Unit = {
    val tableParams = mySQLParams.mySQLTableParams match {
      case Some(tableParams) => tableParams
      case None => throw InvalidStateException("Table Params should have been defined")
    }

    val allKeys = rows.head.keys.toSeq // Assuming all rows have the same columns
    val columnNames = allKeys.mkString(", ")
    val valuePlaceholders = allKeys.map(_ => "?").mkString(", ")

    val sql = s"INSERT INTO ${tableParams.dbName}.${tableParams.tableName} ($columnNames) VALUES ($valuePlaceholders);"
    val preparedStatement = connection.prepareStatement(sql)

    try {
      for (row <- rows) {
        for (i <- 1 to allKeys.length) {
          preparedStatement.setObject(i, row(s"${allKeys(i - 1)}"))
        }
        preparedStatement.addBatch() // Add the current row to the batch
      }
      preparedStatement.executeBatch() // Execute the batch insert
    } finally {
      preparedStatement.close()
    }
  }

  override def readData(perfixQuery: PerfixQuery): Seq[Map[String, Any]] = {
    val query = PerfixQueryConverter.convert(mySQLParams.mySQLTableParams.get, perfixQuery)
    val statement = connection.createStatement()
    val result = resultSetToSeqMap(statement.executeQuery(query))
    statement.close()
    result
  }

  private def createTableStatement(tableName: String, columns: Seq[ColumnDescription]): String = {
    val columnDefs = columns.map(col => s"${col.columnName} ${toSqlType(col.columnType)}").mkString(", ")
    s"CREATE TABLE $tableName ($columnDefs);"
  }

  private def createTableIndexesStatement(primaryIndexColumnName: Option[String],
                                          secondaryIndexesColumnNames: Option[Seq[String]]): String = {
    val tableName = mySQLParams.mySQLTableParams match {
      case Some(tableParams) => tableParams.tableName
      case None => throw InvalidStateException("Table Params should have been defined")
    }

    val primaryIndexSQL = primaryIndexColumnName.map(primaryColumn => s"ADD PRIMARY KEY ($primaryColumn)").getOrElse("")
    val secondaryIndexSQL = secondaryIndexesColumnNames.map { secondaryColumns =>
      if (secondaryColumns.nonEmpty) {
        secondaryColumns.map(columnName => s"ADD INDEX idx_$columnName ($columnName)").mkString(", ")
      } else {
        ""
      }
    }.getOrElse("")

    val indexStatements = Seq(primaryIndexSQL, secondaryIndexSQL).filter(_.nonEmpty).mkString(", ")

    if (indexStatements.nonEmpty) {
      s"ALTER TABLE $tableName $indexStatements;"
    } else {
      ""
    }
  }

  override def cleanup(): Unit = {
    val tableParams = mySQLParams.mySQLTableParams match {
      case Some(tableParams) => tableParams
      case None => throw InvalidStateException("Table Params should have been defined")
    }
    val statement = s"DROP TABLE ${tableParams.dbName}.${tableParams.tableName}"
    val preparedStatement = connection.prepareStatement(statement)
    preparedStatement.execute()
    connection.close()
  }

  private def resultSetToSeqMap(resultSet: ResultSet): Seq[Map[String, Any]] = {
    val metaData = resultSet.getMetaData
    val columnCount = metaData.getColumnCount
    val buffer = scala.collection.mutable.Buffer[Map[String, Any]]()

    while (resultSet.next()) {
      val row = (1 to columnCount).map { i =>
        metaData.getColumnName(i) -> resultSet.getObject(i)
      }.toMap
      buffer += row
    }

    buffer.toSeq
  }
}

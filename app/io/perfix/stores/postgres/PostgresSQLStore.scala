package io.perfix.stores.postgres

import io.perfix.exceptions.{InvalidStateException, WrongQueryException}
import io.perfix.model.ColumnType.toSqlType
import io.perfix.model.{AddressType, ColumnDescription, NameType}
import io.perfix.model.api.{DatabaseConfigParams, DatasetParams}
import io.perfix.model.store.DatabaseSetupParams
import io.perfix.query.{DBQuery, NoSqlDBQuery, SqlDBQuery, SqlDBQueryBuilder}
import io.perfix.stores.DataStore
import io.perfix.stores.mysql.RDSDatabaseSetupParams
import play.api.libs.json.Json

import java.sql.{Connection, DriverManager, ResultSet}

class PostgreSQLStore(datasetParams: DatasetParams,
                      override val databaseConfigParams: RDSDatabaseSetupParams)
  extends DataStore {

  private[stores] var connection: Connection = _

  override val kindOfQuery: String = DBQuery.Sql

  def connectAndInitialize(): Unit = {
    val connectionParams = databaseConfigParams.dbDetails match {
      case Some(con) => con
      case None => throw InvalidStateException("Connection should have been defined")
    }
    import connectionParams._

    Class.forName("org.postgresql.Driver").newInstance
    connection = DriverManager.getConnection(url, username, password)
    val statement = connection.createStatement()

    val db = databaseConfigParams.dbName match {
      case Some(dbName) => dbName
      case None => throw InvalidStateException("Database Name should have been defined")
    }

    val sql = createTableStatement(databaseConfigParams.tableName, datasetParams.getColumns)
    println("Adding table: " + sql)
    statement.executeUpdate(sql)

    val sqls = createTableIndexesStatement(databaseConfigParams.primaryIndexColumn, databaseConfigParams.secondaryIndexesColumn).tail
    if (sqls.nonEmpty) {
      sqls.map { sql =>
        println(sql)
        statement.execute(sql)
      }
    }

    statement.close()
  }

  override def putData(rows: Seq[Map[String, Any]]): Unit = {
    import databaseConfigParams._
    val db = databaseConfigParams.dbName match {
      case Some(dbName) => dbName
      case None => throw InvalidStateException("Database Name should have been defined")
    }

    val allKeys = rows.head.keys.toSeq
    val columnNames = allKeys.mkString(", ")
    val valuePlaceholders = allKeys.map(_ => "?").mkString(", ")

    val sql = s"INSERT INTO ${databaseConfigParams.tableName} ($columnNames) VALUES ($valuePlaceholders);"
    val preparedStatement = connection.prepareStatement(sql)

    try {
      for (row <- rows) {
        for (i <- 1 to allKeys.length) {
          preparedStatement.setObject(i, row(s"${allKeys(i - 1)}"))
        }
        preparedStatement.addBatch()
      }
      preparedStatement.executeBatch()
    } finally {
      preparedStatement.close()
    }
  }

  override def readData(dbQuery: DBQuery): Seq[Map[String, Any]] = {
    val sqlDBQuery: SqlDBQuery = dbQuery match {
      case _: NoSqlDBQuery => throw WrongQueryException("No Sql query not supported")
      case sqlDBQuery: SqlDBQuery => sqlDBQuery
      case sqlDBQueryBuilder: SqlDBQueryBuilder => sqlDBQueryBuilder.convert
    }

    import databaseConfigParams._
    val query = sqlDBQuery.sql
    val statement = connection.createStatement()
    val result = resultSetToSeqMap(statement.executeQuery(query))
    statement.close()
    result
  }

  private def createTableStatement(tableName: String, columns: Seq[ColumnDescription]): String = {
    val columnDefs = columns.map(col => s"${col.columnName} ${toSqlType(col.columnType)}").mkString(", ")
    s"CREATE TABLE IF NOT EXISTS $tableName ($columnDefs);"
  }

  private def createTableIndexesStatement(primaryIndexColumnName: Option[String],
                                          secondaryIndexesColumnNames: Option[Seq[String]]): Seq[String] = {
    val tableName = databaseConfigParams.tableName

    val primaryIndexSQL = primaryIndexColumnName.map(primaryColumn => s"ALTER TABLE $tableName ADD PRIMARY KEY ($primaryColumn)").getOrElse("")
    val secondaryIndexSQL = secondaryIndexesColumnNames.map { secondaryColumns =>
      if (secondaryColumns.nonEmpty) {
        secondaryColumns.map(columnName => s"CREATE INDEX IF NOT EXISTS idx_$columnName ON $tableName ($columnName)")
      } else {
        Seq.empty
      }
    }.getOrElse(Seq.empty)

    Seq(primaryIndexSQL) ++ secondaryIndexSQL
  }

  override def cleanup(): Unit = {
    val db = databaseConfigParams.dbName match {
      case Some(dbName) => dbName
      case None => throw InvalidStateException("Database Name should have been defined")
    }
    val statement = s"DROP TABLE ${databaseConfigParams.tableName}"
    println(statement)
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


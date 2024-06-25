package io.perfix.stores.postgres

import io.perfix.exceptions.InvalidStateException
import io.perfix.model.ColumnType.toSqlType
import io.perfix.model.ColumnDescription
import io.perfix.model.api.DatasetParams
import io.perfix.query.PerfixQuery
import io.perfix.stores.DataStore
import io.perfix.stores.mysql.RDSDatabaseSetupParams

import java.sql.{Connection, DriverManager, ResultSet}

class PostgreSQLStore(datasetParams: DatasetParams,
                      override val databaseConfigParams: RDSDatabaseSetupParams)
  extends DataStore {

  private[stores] var connection: Connection = _

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

    val sql = createTableStatement(databaseConfigParams.tableName, datasetParams.columns)
    println(sql)
    statement.executeUpdate(sql)

    val indexSql = createTableIndexesStatement(databaseConfigParams.primaryIndexColumn, databaseConfigParams.secondaryIndexesColumn)
    if (indexSql.nonEmpty) {
      println(indexSql)
      statement.executeUpdate(indexSql)
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

    val sql = s"INSERT INTO ${tableName} ($columnNames) VALUES ($valuePlaceholders);"
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

  override def readData(perfixQuery: PerfixQuery): Seq[Map[String, Any]] = {
    import databaseConfigParams._
    val query = PostgreSQLStore.convert(dbName.get, tableName, perfixQuery)
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
    val tableName = databaseConfigParams.tableName

    val primaryIndexSQL = primaryIndexColumnName.map(primaryColumn => s"ADD PRIMARY KEY ($primaryColumn)").getOrElse("")
    val secondaryIndexSQL = secondaryIndexesColumnNames.map { secondaryColumns =>
      if (secondaryColumns.nonEmpty) {
        secondaryColumns.map(columnName => s"CREATE INDEX idx_$columnName ON $tableName ($columnName)").mkString(", ")
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

object PostgreSQLStore {
  def convert(dbName: String, tableName: String, perfixQuery: PerfixQuery): String = {
    import perfixQuery._
    val sqlFilterPart = filtersOpt match {
      case Some(filters) => "where " + filters.map(_.toString).mkString(" and ")
      case None => ""
    }

    val projectedFieldsPart = projectedFieldsOpt match {
      case Some(projectedFields) => projectedFields.mkString(", ")
      case None => "*"
    }

    val limitPart = limitOpt match {
      case Some(limit) => s"limit $limit"
      case None => ""
    }

    s"select $projectedFieldsPart from ${tableName} ${sqlFilterPart} ${limitPart}"
  }
}
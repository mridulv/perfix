package io.perfix.experiment

import io.perfix.model.api.Dataset
import io.perfix.model.experiment.{ExperimentParams, SingleExperimentResult}
import io.perfix.query.{DBQuery, NoSqlDBQuery, SqlDBQuery, SqlDBQueryBuilder}
import io.perfix.stores.DataStore
import io.perfix.util.BenchmarkUtil

import scala.collection.mutable.ListBuffer

class SimplePerformanceExperiment(dataStore: DataStore,
                                  experimentParams: ExperimentParams,
                                  dataset: Dataset) extends Experiment {

  def init(): Unit = {
    println(s"Initializing experiment")
    dataStore.connectAndInitialize()
  }

  def run(): SingleExperimentResult = {
    println(s"Running experiment")
    var rowsCount = 0
    val writeTimes = ListBuffer[Long]()
    dataset.data.grouped(experimentParams.writeBatchSize).foreach { rows =>
      try {
        val start = System.currentTimeMillis()
        dataStore.putData(rows)
        val end = System.currentTimeMillis()
        writeTimes.append(end - start)
      } catch {
        case e: Exception =>
          println(s"Error while adding data: ${e.getMessage}")
      }
      rowsCount += rows.size
      println(s"Added $rowsCount to the store")
    }
    val results = try {
      println(s"Starting with the experiment")
      val results = BenchmarkUtil.runBenchmark(
        concurrentThreads = experimentParams.concurrentQueries,
        benchmarkTimeSeconds = experimentParams.experimentTimeInSeconds.getOrElse(60).toLong,
        runTask = () => dataStore.readData(getUpdatedDbQuery(experimentParams.dbQuery)).size
      )
      results
    } catch {
      case e: Exception =>
        println(s"Error while running benchmark: ${e.getMessage}")
        throw e
    }
    results.copy(
      overallWriteTimeTaken = writeTimes.sum,
      writeLatencies = BenchmarkUtil.printPercentiles(writeTimes.sorted.toSeq)
    )
  }

  def cleanup(): Unit = {
    dataStore.cleanup()
  }

  private def getUpdatedDbQuery(dbQuery: DBQuery): DBQuery = {
    val columnNameToValueMapping = dataset.columns.map { column =>
      (column.columnName, column.columnType.getValue)
    }.toMap
    if (dataStore.kindOfQuery == DBQuery.NoSql) {
      dbQuery match {
        case noSqlDBQuery: NoSqlDBQuery => noSqlDBQuery.resolve(columnNameToValueMapping)
        case sqlDBQuery: SqlDBQuery => sqlDBQuery.resolve(columnNameToValueMapping).toNoSqlDBQuery
        case sqlDBQueryBuilder: SqlDBQueryBuilder => sqlDBQueryBuilder.convert.resolve(columnNameToValueMapping).toNoSqlDBQuery
      }
    } else if (dataStore.kindOfQuery == DBQuery.Sql) {
      dbQuery match {
        case noSqlDBQuery: NoSqlDBQuery => throw new RuntimeException(s"Invalid Query: $noSqlDBQuery")
        case sqlDBQuery: SqlDBQuery => sqlDBQuery.resolve(columnNameToValueMapping)
        case sqlDBQueryBuilder: SqlDBQueryBuilder => sqlDBQueryBuilder.convert.resolve(columnNameToValueMapping)
      }
    } else {
      throw new RuntimeException(s"Invalid Query: $dbQuery")
    }
  }
}

package io.perfix.experiment

import io.perfix.generator.FakeDataGenerator
import io.perfix.model.{AddressType, ColumnDescription, DataDescription, NameType, NumericType}
import io.perfix.stores.mysql.{MySQLConnectionParams, MySQLStore, MySQLTableParams, PerfixQueryConverter}
import io.perfix.query.{PerfixQuery, PerfixQueryFilter}
import io.perfix.experiment.SimplePerformanceExperiment
import org.openjdk.jmh.annotations._

@State(Scope.Benchmark)
@BenchmarkMode(Array(Mode.AverageTime, Mode.Throughput))
class SimplePerformanceExperimentBenchmark {

  val fakeDataGenerator = new FakeDataGenerator
  var simplePerformanceExperiment: SimplePerformanceExperiment = _
  val tableParams = MySQLTableParams("arcdraw", "benchTest")

  val columnDescriptions = Seq(
      ColumnDescription("name", NameType),
      ColumnDescription("address", AddressType)
  )

  val query = PerfixQuery(
      filtersOpt = Some(List(PerfixQueryFilter("name", "John"))),
      projectedFieldsOpt = Some(List("name")),
      limitOpt = Some(10)
  )

  @Setup(Level.Trial)
  def setup(): Unit = {
    val dataDescription = new DataDescription()
    dataDescription.rowsOpt = Some(100)
    dataDescription.columnsOpt = Some(columnDescriptions)

    val mysqlStore = new MySQLStore(fakeDataGenerator.generateData(dataDescription))

    val questionnaire = mysqlStore.questions()
    questionnaire.mySQLParams.mySQLTableParams = Some(MySQLTableParams("arcdraw", "benchTest"))
    questionnaire.mySQLParams.mySQLConnectionParams = Some(MySQLConnectionParams("jdbc:mysql://localhost:3306/arcdraw?autoReconnect=true&useSSL=false", "root", "B99@Jake"))

    simplePerformanceExperiment = new SimplePerformanceExperiment(mysqlStore)
    simplePerformanceExperiment.init()
  }

  @Benchmark
  def readDataBenchmark(): Unit = {
    simplePerformanceExperiment.run()
  }
}

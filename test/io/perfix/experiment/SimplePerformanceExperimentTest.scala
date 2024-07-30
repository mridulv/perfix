package io.perfix.experiment

import io.perfix.model.api.{DatabaseConfigDetails, DatabaseConfigId, Dataset}
import io.perfix.model.experiment.{ExperimentParams, ExperimentState}
import io.perfix.query.{SqlDBQueryBuilder, DbQueryFilter}
import io.perfix.stores.mysql.MySQLStore
import org.mockito.Mockito._
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.util.Random

class SimplePerformanceExperimentTest extends AnyFlatSpec with Matchers {
  "SimplePerformanceExperiment" should  "initialize, run, and clean up the experiment" ignore {
    // Create a mock DataStore
    val dataStore = mock(classOf[MySQLStore])

    // Create a mock PerfixQuery
    val perfixQuery = SqlDBQueryBuilder(
      filtersOpt = Some(List(DbQueryFilter("student_name", "John"))),
      projectedFieldsOpt = Some(List("student_name")),
      tableName = "table"
    )

    val experimentParams = ExperimentParams(
      None,
      name = s"exp-${Random.nextInt()}",
      concurrentQueries = 10,
      experimentTimeInSeconds = 5,
      dbQuery = SqlDBQueryBuilder(tableName = "table"),
      databaseConfigs = Seq(DatabaseConfigDetails(DatabaseConfigId(-1))),
      experimentResults = None,
      createdAt = Some(System.currentTimeMillis()),
      experimentState = Some(ExperimentState.Created)
    )
    val experiment = new SimplePerformanceExperiment(dataStore, experimentParams, Dataset.datasetForTesting)

    // Initialize the experiment
    experiment.init()
    var putDataCalled = false
    var readDataCalled = false

    // Verify that connectAndInitialize() is called on the data store
    verify(dataStore).connectAndInitialize()
    when(dataStore.putData(Seq.empty)).thenAnswer(new Answer[Unit] {
      override def answer(invocation: InvocationOnMock): Unit = {
        putDataCalled = true
      }
    })
    when(dataStore.readData(perfixQuery)).thenReturn {
      readDataCalled = true
      Seq.empty
    }

    experiment.run()

    putDataCalled shouldBe true
    readDataCalled shouldBe true

    // Clean up the experiment
    experiment.cleanup()

    // Verify that cleanup() is called on the data store
    verify(dataStore).cleanup()
  }
}
package io.perfix.experiment

import io.perfix.query.{PerfixQuery, PerfixQueryFilter}
import io.perfix.stores.DataStore
import org.mockito.Mockito
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.mockito.Mockito._
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer

class SimplePerformanceExperimentTest extends AnyFlatSpec with Matchers {
  "SimplePerformanceExperiment" should  "initialize, run, and clean up the experiment" in {
    // Create a mock DataStore
    val dataStore = mock(classOf[DataStore])

    // Create a mock PerfixQuery
    val perfixQuery = PerfixQuery(
      filtersOpt = Some(List(PerfixQueryFilter("student_name", "John"))),
      projectedFieldsOpt = Some(List("student_name")),
      limitOpt = Some(10)
    )

    // Create a SimplePerformanceExperiment instance
    val experiment = new SimplePerformanceExperiment(dataStore, perfixQuery)
    experiment.experimentParams.concurrentQueriesOpt = Some(10)
    experiment.experimentParams.dataDescription.rowsOpt = Some(5)
    experiment.experimentParams.dataDescription.columnsOpt = Some(Seq.empty)
    experiment.experimentParams.dataDescription.setData()

    // Initialize the experiment
    experiment.init()
    var putDataCalled = false
    var readDataCalled = false

    // Verify that connectAndInitialize() is called on the data store
    verify(dataStore).connectAndInitialize()
    when(dataStore.putData()).thenAnswer(new Answer[Unit] {
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

  "SimplePerformanceExperiment" should "throw an exception when parameters are not defined correctly" in {
    // Create a mock DataStore
    val dataStore = mock(classOf[DataStore])

    // Create a SimplePerformanceExperiment instance with incorrect parameters
    val experiment = new SimplePerformanceExperiment(dataStore, PerfixQuery())

    // Try to initialize the experiment (should throw an exception)
    an[Exception] should be thrownBy experiment.init()
  }
}
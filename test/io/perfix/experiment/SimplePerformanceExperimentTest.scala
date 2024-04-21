package io.perfix.experiment

import io.perfix.model.{Dataset, ExperimentParams}
import io.perfix.query.{PerfixQuery, PerfixQueryFilter}
import io.perfix.stores.DataStore
import org.mockito.Mockito
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.mockito.Mockito._
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer

class SimplePerformanceExperimentTest extends AnyFlatSpec with Matchers {
  "SimplePerformanceExperiment" should  "initialize, run, and clean up the experiment" ignore {
    // Create a mock DataStore
    val dataStore = mock(classOf[DataStore])

    // Create a mock PerfixQuery
    val perfixQuery = PerfixQuery(
      filtersOpt = Some(List(PerfixQueryFilter("student_name", "John"))),
      projectedFieldsOpt = Some(List("student_name")),
      limitOpt = Some(10)
    )

    val experimentParams = ExperimentParams.experimentParamsForTesting
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
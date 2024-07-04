package io.perfix.common

import io.perfix.experiment.SimplePerformanceExperiment
import io.perfix.model.experiment.{ExperimentParams, SingleExperimentResult}
import io.perfix.model.api.{DatabaseConfigParams, Dataset, DatasetParams}
import io.perfix.stores.Database

class ExperimentExecutor(experimentParams: ExperimentParams,
                         databaseConfigParams: DatabaseConfigParams,
                         datasetParams: DatasetParams) {

  private val dataStore = Database.getStore(databaseConfigParams, datasetParams)

  private val experiment = new SimplePerformanceExperiment(dataStore,
    experimentParams,
    datasetParams.datasets.datasets.head
  )

  def runExperiment(): SingleExperimentResult = {
    experiment.init()
    experiment.run()
  }

  def cleanUp(): Unit = {
    experiment.cleanup()
  }

}


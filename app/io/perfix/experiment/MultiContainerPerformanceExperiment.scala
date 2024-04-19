package io.perfix.experiment
import io.perfix.model.{ExperimentRunParams, ExperimentResult}

class MultiContainerPerformanceExperiment extends Experiment {

  override def init: Unit = ???

  override def repopulateExperimentParams(experimentRunParams: ExperimentRunParams): Unit = ???

  override def run(): ExperimentResult = ???

  override def cleanup(): Unit = ???
}

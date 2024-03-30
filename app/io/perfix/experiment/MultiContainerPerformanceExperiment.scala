package io.perfix.experiment
import io.perfix.model.{ExperimentRunParams, PerfixExperimentResult}

class MultiContainerPerformanceExperiment extends Experiment {

  override def init: Unit = ???

  override def repopulateExperimentParams(experimentRunParams: ExperimentRunParams): Unit = ???

  override def run(): PerfixExperimentResult = ???

  override def cleanup(): Unit = ???
}

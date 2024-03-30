package io.perfix.experiment

import io.perfix.model.{ExperimentRunParams, PerfixExperimentResult}

trait Experiment {

  def init(): Unit

  def repopulateExperimentParams(experimentRunParams: ExperimentRunParams): Unit

  def run(): PerfixExperimentResult

  def cleanup(): Unit

}

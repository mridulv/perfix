package io.perfix.experiment

import io.perfix.model.{ExperimentRunParams, ExperimentResult}

trait Experiment {

  def init(): Unit

  def repopulateExperimentParams(experimentRunParams: ExperimentRunParams): Unit

  def run(): ExperimentResult

  def cleanup(): Unit

}

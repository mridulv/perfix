package io.perfix.experiment

import io.perfix.model.experiment.ExperimentResult

trait Experiment {

  def init(): Unit

  def run(): ExperimentResult

  def cleanup(): Unit

}

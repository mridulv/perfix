package io.perfix.experiment

import io.perfix.model.experiment.SingleExperimentResult

trait Experiment {

  def init(): Unit

  def run(): SingleExperimentResult

  def cleanup(): Unit

}

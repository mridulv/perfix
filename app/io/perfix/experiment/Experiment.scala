package io.perfix.experiment

import io.perfix.model.ExperimentResult

trait Experiment {

  def init(): Unit

  def run(): ExperimentResult

  def cleanup(): Unit

}

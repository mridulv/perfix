package io.perfix.examples

import io.perfix.experiment.SimplePerformanceExperiment
import io.perfix.stores.mysql.MySQLStore

object MySQLExample {

  def main(args: Array[String]): Unit = {
    val simplePerformanceExperiment = new SimplePerformanceExperiment(new MySQLStore())
  }
}

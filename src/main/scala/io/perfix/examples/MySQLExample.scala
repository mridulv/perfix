package io.perfix.examples

import io.perfix.context.MappedQuestionExecutionContext
import io.perfix.experiment.SimplePerformanceExperiment
import io.perfix.stores.mysql.MySQLStore

object MySQLExample {

  def main(args: Array[String]): Unit = {
    val questionExecutionContext = new MappedQuestionExecutionContext(Map.empty)
    val mySQLStore = new MySQLStore(questionExecutionContext)
    val simplePerformanceExperiment = new SimplePerformanceExperiment(mySQLStore, questionExecutionContext)
  }
}

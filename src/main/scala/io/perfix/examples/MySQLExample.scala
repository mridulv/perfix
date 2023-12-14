package io.perfix.examples

import io.perfix.context.MappedQuestionExecutionContext
import io.perfix.experiment.SimplePerformanceExperiment
import io.perfix.stores.mysql.MySQLStore

object MySQLExample {

  def main(args: Array[String]): Unit = {
    val mappedVariables: Map[String, Any] = Map[String, Any]().empty
    val questionExecutionContext = new MappedQuestionExecutionContext(mappedVariables)
    val mySQLStore = new MySQLStore(questionExecutionContext)

    val simplePerformanceExperiment = new SimplePerformanceExperiment(mySQLStore, questionExecutionContext)
    simplePerformanceExperiment.init()
    simplePerformanceExperiment.run()
  }
}

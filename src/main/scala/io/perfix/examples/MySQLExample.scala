package io.perfix.examples

import io.perfix.context.MappedQuestionExecutionContext
import io.perfix.experiment.SimplePerformanceExperiment
import io.perfix.question.experiment.DataQuestions._
import io.perfix.question.experiment.ExperimentParamsQuestion.CONCURRENT_QUERIES
import io.perfix.question.mysql.ConnectionParamsQuestion._
import io.perfix.question.mysql.TableParamsQuestions.{DBNAME, TABLENAME}
import io.perfix.stores.mysql.MySQLStore

object MySQLExample {

  def main(args: Array[String]): Unit = {
    val mappedVariables: Map[String, Any] = Map(
      ROWS -> 100000,
      COLUMNS -> "name,address",
      CONCURRENT_QUERIES -> 10,
      URL -> "jdbc:mysql://localhost:3306/arcdraw?autoReconnect=true&useSSL=false&allowPublicKeyRetrieval=true",
      USERNAME -> "root",
      PASSWORD -> "*********",
      DBNAME -> "arcdraw",
      TABLENAME -> "test"
    )
    val questionExecutionContext = new MappedQuestionExecutionContext(mappedVariables)
    val mySQLStore = new MySQLStore(questionExecutionContext)

    val simplePerformanceExperiment = new SimplePerformanceExperiment(mySQLStore, questionExecutionContext)
    val iter = simplePerformanceExperiment.questions().getQuestions
    while (iter.hasNext) {
      val question = iter.next()
      question.evaluateQuestion()
    }
    simplePerformanceExperiment.init()
    simplePerformanceExperiment.run()
    simplePerformanceExperiment.cleanup()
  }
}

package io.perfix.examples

import io.perfix.context.MappedQuestionExecutionContext
import io.perfix.experiment.SimplePerformanceExperiment
import io.perfix.query.{PerfixQuery, PerfixQueryFilter}
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
      URL -> "jdbc:mysql://localhost:3306/perfix?autoReconnect=true&useSSL=false&allowPublicKeyRetrieval=true",
      USERNAME -> "root",
      PASSWORD -> "**********",
      DBNAME -> "perfix",
      TABLENAME -> "test"
    )
    val questionExecutionContext = new MappedQuestionExecutionContext(mappedVariables)
    val mySQLStore = new MySQLStore(questionExecutionContext)
    val perfixQuery = PerfixQuery(
      filtersOpt = Some(List(PerfixQueryFilter("name", "John"))),
      projectedFieldsOpt = Some(List("name")),
      limitOpt = Some(10)
    )

    val simplePerformanceExperiment = new SimplePerformanceExperiment(
      mySQLStore,
      perfixQuery,
      questionExecutionContext
    )
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

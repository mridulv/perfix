package io.perfix.examples

import io.perfix.common.PerfixExperimentExecutor
import io.perfix.question.experiment.DataQuestions._
import io.perfix.question.experiment.ExperimentParamsQuestion.CONCURRENT_QUERIES
import io.perfix.question.mysql.ConnectionParamsQuestion._
import io.perfix.question.mysql.TableParamsQuestions.{DBNAME, TABLENAME}

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
    val experimentExecutor = new PerfixExperimentExecutor("mysql")
    while (experimentExecutor.getQuestionnaireExecutor.hasNext) {
      val question = experimentExecutor.getQuestionnaireExecutor.next()
      val answerMapping = question.map { case (k, _) =>
        k -> mappedVariables(k)
      }
      experimentExecutor.getQuestionnaireExecutor.submit(answerMapping)
    }

    experimentExecutor.runExperiment()
    experimentExecutor.cleanUp()
  }
}

package io.perfix.examples

import io.perfix.common.PerfixExperimentExecutor
import io.perfix.question.Question
import io.perfix.question.experiment.DataQuestions._
import io.perfix.question.experiment.ExperimentParamsQuestion.CONCURRENT_QUERIES
import io.perfix.question.mysql.ConnectionParamsQuestion._
import io.perfix.question.mysql.TableParamsQuestions.{DBNAME, TABLENAME}

object MySQLExample {

  def main(args: Array[String]): Unit = {
    val mappedVariables: Map[String, Any] = Map(
      ROWS -> 100000,
      COLUMNS -> "[{\"columnName\":\"name\",\"columnType\":{\"constraint\":{\"startEpoch\":1,\"endEpoch\":2},\"type\":\"EpochType\"}},{\"columnName\":\"address\",\"columnType\":{\"type\":\"EpochType\"}}]",
      CONCURRENT_QUERIES -> 10,
      URL -> "jdbc:mysql://localhost:3306/perfix?autoReconnect=true&useSSL=false&allowPublicKeyRetrieval=true",
      USERNAME -> "root",
      PASSWORD -> "mridulv09",
      DBNAME -> "perfix",
      TABLENAME -> "test"
    )
    val experimentExecutor = new PerfixExperimentExecutor("mysql")
    while (experimentExecutor.getQuestionnaireExecutor.hasNext) {
      val question = experimentExecutor.getQuestionnaireExecutor.next()
      val answerMapping = question.map { case (k, questionType) =>
        val mappedValue = if (questionType.isRequired) {
          Some(mappedVariables(k))
        } else {
          mappedVariables.get(k)
        }
        k -> mappedValue
      }
      experimentExecutor.getQuestionnaireExecutor.submit(Question.filteredAnswers(answerMapping))
    }

    experimentExecutor.runExperiment()
    experimentExecutor.cleanUp()
  }
}

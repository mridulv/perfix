package io.perfix.examples

import io.perfix.common.PerfixExperimentExecutor
import io.perfix.model.{AddressType, ColumnDescription, NameType}
import io.perfix.question.Question
import io.perfix.question.experiment.DataQuestions._
import io.perfix.question.experiment.ExperimentParamsQuestion.CONCURRENT_QUERIES
import io.perfix.question.mysql.ConnectionParamsQuestion._
import io.perfix.question.mysql.TableIndicesDetailQuestion.PRIMARY_INDEX_COLUMN
import io.perfix.question.mysql.TableParamsQuestions.{DBNAME, TABLENAME}
import play.api.libs.json.Json

object MySQLExample {

  def main(args: Array[String]): Unit = {
    val mappedVariables: Map[String, Any] = Map(
      ROWS -> 100000,
      COLUMNS -> "[{\"columnName\":\"name\",\"columnType\":{\"type\":\"NameType\",\"isUnique\":true}},{\"columnName\":\"address\",\"columnType\":{\"type\":\"AddressType\",\"isUnique\":false}}]",
      CONCURRENT_QUERIES -> 10,
      URL -> "jdbc:mysql://localhost:3306/perfix?autoReconnect=true&useSSL=false&allowPublicKeyRetrieval=true",
      USERNAME -> "root",
      PASSWORD -> "*********",
      DBNAME -> "perfix",
      TABLENAME -> "test",
      PRIMARY_INDEX_COLUMN -> "name"
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

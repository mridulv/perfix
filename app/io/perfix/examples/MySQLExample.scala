package io.perfix.examples

import io.perfix.common.PerfixExperimentExecutor
import io.perfix.question.AWSCloudParamsQuestion._
import io.perfix.question.Question
import io.perfix.question.experiment.DataQuestions._
import io.perfix.question.experiment.ExperimentParamsQuestion.CONCURRENT_QUERIES
import io.perfix.question.mysql.ConnectionParamsQuestion._
import io.perfix.question.mysql.MySQLLaunchQuestion._
import io.perfix.question.mysql.TableIndicesDetailQuestion.{PRIMARY_INDEX_COLUMN, SECONDARY_INDEX_COLUMNS}
import io.perfix.question.mysql.TableParamsQuestions.{DBNAME, TABLENAME}

object MySQLExample {

  def main(args: Array[String]): Unit = {
    val mappedVariables: Map[String, Any] = Map(
      ROWS -> 1000,
      COLUMNS -> "[{\"columnName\":\"student_name\",\"columnType\":{\"type\":\"NameType\",\"isUnique\":true}},{\"columnName\":\"student_address\",\"columnType\":{\"type\":\"AddressType\",\"isUnique\":false}}]",
      CONCURRENT_QUERIES -> 10,
      URL -> "jdbc:mysql://localhost:3306/perfix?autoReconnect=true&useSSL=false&allowPublicKeyRetrieval=true",
      USERNAME -> "root",
      PASSWORD -> "*********",
      DBNAME -> "perfix",
      TABLENAME -> "test",
      PRIMARY_INDEX_COLUMN -> "student_name",
      SECONDARY_INDEX_COLUMNS -> "student_name,student_address",
      INSTANCE_IDENTIFIER -> "dbinstance",
      INSTANCE_TYPE -> "db.t4g.micro",
      AWS_ACCESS_KEY -> "************",
      AWS_ACCESS_SECRET -> "************************************"
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

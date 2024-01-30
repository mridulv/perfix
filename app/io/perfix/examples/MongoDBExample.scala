package io.perfix.examples

import io.perfix.common.PerfixExperimentExecutor
import io.perfix.question.Question
import io.perfix.question.documentdb.DocumentDBConnectionParamsQuestion._
import io.perfix.question.documentdb.DocumentDBTableParamsQuestions.COLLECTION_NAME
import io.perfix.question.experiment.DataQuestions._
import io.perfix.question.experiment.ExperimentParamsQuestion.CONCURRENT_QUERIES

object MongoDBExample {

  def main(args: Array[String]): Unit = {
    val mappedVariables: Map[String, Any] = Map(
      ROWS -> 10000,
      COLUMNS -> "[{\"columnName\":\"student_name\",\"columnType\":{\"type\":\"NameType\",\"isUnique\":true}},{\"columnName\":\"student_address\",\"columnType\":{\"type\":\"AddressType\",\"isUnique\":false}}]",
      CONCURRENT_QUERIES -> 10,
      URL -> "localhost:27017",
      DATABASE -> "test",
      COLLECTION_NAME -> "students"
      COLLECTION_NAME -> "students",
      INDICES_COLUMNS -> "{\"columns\":[\"student_name\"]}"
    )
    val experimentExecutor = new PerfixExperimentExecutor("mongodb")
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

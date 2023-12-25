package io.perfix.examples

import io.perfix.context.MappedQuestionExecutionContext
import io.perfix.experiment.SimplePerformanceExperiment
import io.perfix.question.dynamodb.DynamoDBConnectionParametersQuestions.{ACCESS_ID, ACCESS_SECRET, CONNECTION_URL}
import io.perfix.question.dynamodb.DynamoDBTableParamsQuestions._
import io.perfix.question.experiment.DataQuestions._
import io.perfix.question.experiment.ExperimentParamsQuestion.CONCURRENT_QUERIES
import io.perfix.stores.dynamodb.DynamoDBStore

object DynamoDBExample {

  def main(args: Array[String]): Unit = {
    val mappedVariables: Map[String, Any] = Map(
      ROWS -> 100000,
      COLUMNS -> "name2,address2",
      CONCURRENT_QUERIES -> 10,
      TABLE_NAME -> "test4",
      PARTITION_KEY -> "name2",
      SORT_KEY -> "address2",
      CONNECTION_URL -> "http://localhost:8000",
      ACCESS_ID -> "azd12345",
      ACCESS_SECRET -> "azd12345aaa"
    )
    val questionExecutionContext = new MappedQuestionExecutionContext(mappedVariables)
    val mySQLStore = new DynamoDBStore(questionExecutionContext)

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

package io.perfix.examples

import io.perfix.context.MappedQuestionExecutionContext
import io.perfix.experiment.SimplePerformanceExperiment
import io.perfix.query.{PerfixQuery, PerfixQueryFilter}
import io.perfix.question.dynamodb.DynamoDBConnectionParametersQuestions.{ACCESS_ID, ACCESS_SECRET, CONNECTION_URL}
import io.perfix.question.dynamodb.DynamoDBTableParamsQuestions._
import io.perfix.question.experiment.DataQuestions._
import io.perfix.question.experiment.ExperimentParamsQuestion.CONCURRENT_QUERIES
import io.perfix.stores.dynamodb.DynamoDBStore

object DynamoDBExample {

  def main(args: Array[String]): Unit = {
    val mappedVariables: Map[String, Any] = Map(
      ROWS -> 100000,
      COLUMNS -> "student_name,student_address",
      CONCURRENT_QUERIES -> 10,
      TABLE_NAME -> "test",
      PARTITION_KEY -> "student_name",
      SORT_KEY -> "student_address",
      CONNECTION_URL -> "http://localhost:8000",
      ACCESS_ID -> "**********",
      ACCESS_SECRET -> "**********"
    )
    val questionExecutionContext = new MappedQuestionExecutionContext(mappedVariables)
    val dynamoDBStore = new DynamoDBStore(questionExecutionContext)
    val perfixQuery = PerfixQuery(
      filtersOpt = Some(List(PerfixQueryFilter("student_name", "John"))),
      projectedFieldsOpt = Some(List("student_name")),
      limitOpt = Some(10)
    )

    val simplePerformanceExperiment = new SimplePerformanceExperiment(
      dynamoDBStore,
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

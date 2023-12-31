package io.perfix

import io.perfix.context.{MappedQuestionExecutionContext, QuestionExecutionContext}
import io.perfix.experiment.SimplePerformanceExperiment
import io.perfix.model._
import io.perfix.query.{PerfixQuery, PerfixQueryFilter}
import io.perfix.question.Question.QuestionLabel
import io.perfix.question.Questionnaire
import io.perfix.stores.DataStore
import io.perfix.stores.dynamodb.{DynamoDBParams, DynamoDBQuestionnaire, DynamoDBStore}
import io.perfix.stores.mysql.{MySQLParams, MySQLQuestionnaire, MySQLStore}

import javax.inject.Singleton
import scala.collection.mutable
import scala.util.Random

@Singleton
class PerfixManager {
  val mapping: mutable.Map[Int, PerfixQuestionAnswerMapping] = mutable.Map.empty[Int, PerfixQuestionAnswerMapping]

  def startQuestionnaire(storeName: String): PerfixQuestionnaire = {
    val questionnaire = getQuestionnaire(storeName)
    val questionsMapped = PerfixQuestionAnswerMapping(
      storeName,
      questionnaire.getQuestions.map(_.mapping),
      mutable.ListBuffer.empty
    )
    val perfixQuestionnaire = PerfixQuestionnaire(
      Random.nextInt(),
      questionnaire.getQuestions.toList.length
    )
    mapping.put(perfixQuestionnaire.id, questionsMapped)
    perfixQuestionnaire
  }

  def startExperiment(questionnaireId: Int): PerfixExperimentResult = {
    val questionAnswerMapping = mapping(questionnaireId).answers.toList.flatMap(_.answers).map { e =>
      (e.questionLabel -> e.answer)
    }.toMap
    val executionContext = new MappedQuestionExecutionContext(questionAnswerMapping)
    val perfixQuery = PerfixQuery(
      filtersOpt = Some(List(PerfixQueryFilter("name", "John"))),
      projectedFieldsOpt = Some(List("name")),
      limitOpt = Some(10)
    )
    val dataStore = getDataStore(mapping(questionnaireId).storeName, executionContext)
    val experiment = new SimplePerformanceExperiment(dataStore, perfixQuery, executionContext)
    val iter = experiment.questions().getQuestions
    while (iter.hasNext) {
      val question = iter.next()
      question.evaluateQuestion()
    }
    experiment.init()
    experiment.run()
    experiment.cleanup()
    PerfixExperimentResult(questionnaireId)
  }

  def nextQuestion(questionnaireId: Int): PerfixQuestion = {
    PerfixQuestion(mapping(questionnaireId).questions.next())
  }

  def submitQuestionAnswer(questionnaireId: Int,
                           questionAnswers: PerfixQuestionAnswers): Unit = {
    mapping(questionnaireId).answers.append(questionAnswers)
  }

  private def getQuestionnaire(storeName: String): Questionnaire = {
    storeName.strip().toLowerCase() match {
      case "dynamodb" =>
        val dynamoDBParams = DynamoDBParams(DataDescription())
        DynamoDBQuestionnaire(dynamoDBParams, new MappedQuestionExecutionContext(Map.empty))
      case "mysql" =>
        val mySQLParams = MySQLParams(DataDescription())
        MySQLQuestionnaire(mySQLParams, new MappedQuestionExecutionContext(Map.empty))
    }
  }

  private def getDataStore(storeName: String,
                           executionContext: QuestionExecutionContext): DataStore = {
    storeName.strip().toLowerCase() match {
      case "dynamodb" => new DynamoDBStore(executionContext)
      case "mysql" => new MySQLStore(executionContext)
    }
  }

  case class PerfixQuestionAnswerMapping(storeName: String,
                                         questions: Iterator[Map[QuestionLabel, DataType]],
                                         answers: mutable.ListBuffer[PerfixQuestionAnswers])

}
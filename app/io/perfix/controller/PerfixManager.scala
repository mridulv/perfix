package io.perfix.controller

import io.perfix.context.MappedQuestionExecutionContext
import io.perfix.model.{DataDescription, DataType, PerfixExperimentResult, PerfixQuestion, PerfixQuestionAnswer, PerfixQuestionAnswers, PerfixQuestionnaire}
import io.perfix.question.Question.QuestionLabel
import io.perfix.stores.dynamodb.{DynamoDBParams, DynamoDBQuestionnaire}
import io.perfix.stores.mysql.{MySQLParams, MySQLQuestionnaire}

import javax.inject.Singleton
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.util.Random

@Singleton
class PerfixManager {
  val mapping: mutable.Map[Int, PerfixQuestionAnswerMapping] = mutable.Map.empty[Int, PerfixQuestionAnswerMapping]

  def startQuestionnaire(storeName: String): PerfixQuestionnaire = {
    val questionnaire = storeName match {
      case "DynamoDB" =>
        val dynamoDBParams = DynamoDBParams(DataDescription())
        DynamoDBQuestionnaire(dynamoDBParams, new MappedQuestionExecutionContext(Map.empty))
      case "MySQL" =>
        val mySQLParams = MySQLParams(DataDescription())
        MySQLQuestionnaire(mySQLParams, new MappedQuestionExecutionContext(Map.empty))
    }
    val questionsMapped = PerfixQuestionAnswerMapping(
      questionnaire.getQuestions.map(_.mapping),
      mutable.ListBuffer.empty
    )
    val perfixQuestionnaire = PerfixQuestionnaire(Random.nextInt(), questionnaire.getQuestions.toList.length)
    mapping.put(perfixQuestionnaire.id, questionsMapped)
    perfixQuestionnaire
  }

  def startExperiment(questionnaireId: Int): PerfixExperimentResult = {
    null
  }

  def nextQuestion(questionnaireId: Int): PerfixQuestion = {
    PerfixQuestion(mapping(questionnaireId).questions.next())
  }

  def submitQuestionAnswer(questionnaireId: Int,
                           questionAnswers: PerfixQuestionAnswers): Unit = {
    mapping(questionnaireId).answers.append(questionAnswers)
  }

}

case class PerfixQuestionAnswerMapping(questions: Iterator[Map[QuestionLabel, DataType]],
                                       answers: mutable.ListBuffer[PerfixQuestionAnswers])
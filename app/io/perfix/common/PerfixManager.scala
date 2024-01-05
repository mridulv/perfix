package io.perfix.common

import io.perfix.model._

import javax.inject.Singleton
import scala.collection.mutable
import scala.util.Random

@Singleton
class PerfixManager {
  val mapping: mutable.Map[Int, PerfixExperimentExecutor] = mutable.Map.empty[Int, PerfixExperimentExecutor]

  def startQuestionnaire(storeName: String): QuestionnaireResponse = {
    val experimentExecutor = new PerfixExperimentExecutor(storeName)
    val response = QuestionnaireResponse(
      Random.nextInt(),
      experimentExecutor.getQuestionnaireExecutor.length
    )
    mapping.put(response.id, experimentExecutor)
    response
  }

  def startExperiment(questionnaireId: Int): PerfixExperimentResult = {
    mapping(questionnaireId).runExperiment()
    PerfixExperimentResult(questionnaireId)
  }

  def nextQuestion(questionnaireId: Int): PerfixQuestion = {
    PerfixQuestion(mapping(questionnaireId).getQuestionnaireExecutor.next())
  }

  def submitQuestionAnswer(questionnaireId: Int,
                           questionAnswers: PerfixQuestionAnswers): Unit = {
    mapping(questionnaireId).getQuestionnaireExecutor.submit(questionAnswers.toMap)
  }
}
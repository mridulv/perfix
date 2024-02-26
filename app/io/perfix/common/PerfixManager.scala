package io.perfix.common

import io.perfix.model._
import io.perfix.question.Question

import javax.inject.Singleton
import scala.collection.mutable
import scala.util.Random

@Singleton
class PerfixManager {
  val resultsMapping: mutable.Map[Int, PerfixExperimentResultWithMapping] = mutable.Map.empty[Int, PerfixExperimentResultWithMapping]
  val mapping: mutable.Map[Int, PerfixExperimentExecutor] = mutable.Map.empty[Int, PerfixExperimentExecutor]

  def startQuestionnaire(storeName: String): QuestionnaireResponse = {
    val experimentExecutor = new PerfixExperimentExecutor(storeName)
    val response = QuestionnaireResponse(Random.nextInt(1000))
    mapping.put(response.id, experimentExecutor)
    resultsMapping.put(response.id, PerfixExperimentResultWithMapping.empty)
    response
  }

  def nextQuestion(questionnaireId: Int): Option[PerfixQuestion] = {
    val perfixExecutor = mapping(questionnaireId)
    if (perfixExecutor.getQuestionnaireExecutor.hasNext) {
      Some(PerfixQuestion(perfixExecutor.getQuestionnaireExecutor.next()))
    } else {
      None
    }
  }

  def submitQuestionAnswer(questionnaireId: Int,
                           questionAnswers: PerfixQuestionAnswers): Unit = {
    mapping(questionnaireId).getQuestionnaireExecutor.submit(questionAnswers.toMap)
    val perfixExperimentResultWithMapping = resultsMapping(questionnaireId).addPerfixQuestionAnswers(questionAnswers.answers)
    resultsMapping.update(questionnaireId, perfixExperimentResultWithMapping)
  }

  def startExperiment(questionnaireId: Int): PerfixExperimentResult = {
    val experimentResult = mapping(questionnaireId).runExperiment()
    val perfixExperimentResultWithMapping = resultsMapping(questionnaireId).addPerfixExperimentResult(experimentResult)
    resultsMapping.update(questionnaireId, perfixExperimentResultWithMapping)
    mapping(questionnaireId).cleanUp()
    experimentResult
  }

  def executeExperiment(storeName: String,
                        questionAnswers: PerfixQuestionAnswers): QuestionnaireResponse = {
    val mappedVariables = questionAnswers.toMap
    val experimentExecutor = new PerfixExperimentExecutor(storeName)
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

    val response = QuestionnaireResponse(Random.nextInt(1000))
    val result = experimentExecutor.runExperiment()
    resultsMapping.update(response.id, PerfixExperimentResultWithMapping(Some(result), questionAnswers))
    experimentExecutor.cleanUp()
    response
  }
}
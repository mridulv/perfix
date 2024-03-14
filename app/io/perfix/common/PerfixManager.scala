package io.perfix.common

import io.perfix.exceptions.InvalidExperimentException
import io.perfix.model._
import io.perfix.question.Question

import javax.inject.Singleton
import scala.collection.mutable
import scala.util.Random

@Singleton
class PerfixManager {
  val resultsMapping: mutable.Map[Int, PerfixExperimentResultWithMapping] = mutable.Map.empty[Int, PerfixExperimentResultWithMapping]
  val mapping: mutable.Map[Int, PerfixExperimentExecutor] = mutable.Map.empty[Int, PerfixExperimentExecutor]

  def startQuestionnaire(storeName: String): ExperimentId = {
    val experimentExecutor = new PerfixExperimentExecutor(storeName)
    val response = ExperimentId(Random.nextInt(1000))
    println(s"Experiment Id: ${response.id}")
    mapping.put(response.id, experimentExecutor)
    resultsMapping.put(response.id, PerfixExperimentResultWithMapping.empty)
    response
  }

  def nextQuestion(experimentId: Int): Option[PerfixQuestion] = {
    val perfixExecutor = mapping(experimentId)
    if (perfixExecutor.getQuestionnaireExecutor.hasNext) {
      Some(PerfixQuestion(perfixExecutor.getQuestionnaireExecutor.next()))
    } else {
      None
    }
  }

  def submitQuestionAnswer(experimentId: Int,
                           questionAnswers: PerfixQuestionAnswers): Unit = {
    mapping(experimentId).getQuestionnaireExecutor.submit(questionAnswers.toMap)
    val perfixExperimentResultWithMapping = resultsMapping(experimentId).addPerfixQuestionAnswers(questionAnswers.answers)
    resultsMapping.update(experimentId, perfixExperimentResultWithMapping)
  }

  def startExperiment(experimentId: Int): PerfixExperimentResult = {
    val experimentResult = mapping(experimentId).runExperiment()
    val perfixExperimentResultWithMapping = resultsMapping(experimentId).addPerfixExperimentResult(experimentResult)
    resultsMapping.update(experimentId, perfixExperimentResultWithMapping)
    mapping(experimentId).cleanUp()
    experimentResult
  }

  def executeExperiment(storeName: String,
                        questionAnswers: PerfixQuestionAnswers): ExperimentId = {
    val response = ExperimentId(Random.nextInt(1000))
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

    mapping.put(response.id, experimentExecutor)
    val result = experimentExecutor.runExperiment()
    resultsMapping.update(response.id, PerfixExperimentResultWithMapping(Some(result), questionAnswers))
    experimentExecutor.cleanUp()
    println(s"Experiment Id: ${response.id}")
    response
  }

  def repeatExperiment(experimentId: Int,
                       experimentRunParams: ExperimentRunParams): PerfixExperimentResult = {
    val experimentExecutor = mapping.getOrElse(experimentId, throw new InvalidExperimentException(experimentId))
    experimentExecutor.repopulateExperimentParams(experimentRunParams)
    val experimentResult = experimentExecutor.runExperiment()
    experimentExecutor.cleanUp()
    experimentResult
  }

  def results(experimentId: Int): PerfixExperimentResultWithMapping = {
    resultsMapping(experimentId)
  }
}
package io.perfix.manager

import io.perfix.common.ExperimentExecutor
import io.perfix.exceptions.InvalidExperimentException
import io.perfix.model._
import io.perfix.forms.Form

import javax.inject.Singleton
import scala.collection.mutable
import scala.util.Random

@Singleton
class ExperimentManager {
  val resultsMapping: mutable.Map[Int, ExperimentResultWithFormInputValues] = mutable.Map.empty[Int, ExperimentResultWithFormInputValues]
  val mapping: mutable.Map[Int, ExperimentExecutor] = mutable.Map.empty[Int, ExperimentExecutor]

  def executeExperiment(storeName: String,
                        formInputValues: FormInputValues): ExperimentId = {
    val response = ExperimentId(Random.nextInt(1000))
    val mappedVariables = formInputValues.toMap
    val experimentExecutor = new ExperimentExecutor(storeName)
    while (experimentExecutor.getFormSeriesEvaluator.hasNext) {
      val form = experimentExecutor.getFormSeriesEvaluator.next()
      val answerMapping = form.map { case (k, formInputType) =>
        val mappedValue = if (formInputType.isRequired) {
          Some(mappedVariables(k))
        } else {
          mappedVariables.get(k)
        }
        k -> mappedValue
      }
      experimentExecutor.getFormSeriesEvaluator.submit(Form.filteredAnswers(answerMapping))
    }

    mapping.put(response.id, experimentExecutor)
    val result = experimentExecutor.runExperiment()
    resultsMapping.update(response.id, ExperimentResultWithFormInputValues(Some(result), formInputValues))
    experimentExecutor.cleanUp()
    println(s"Experiment Id: ${response.id}")
    response
  }

  def repeatExperiment(experimentId: Int,
                       experimentRunParams: ExperimentRunParams): ExperimentResult = {
    val experimentExecutor = mapping.getOrElse(experimentId, throw new InvalidExperimentException(experimentId))
    experimentExecutor.repopulateExperimentParams(experimentRunParams)
    val experimentResult = experimentExecutor.runExperiment()
    experimentExecutor.cleanUp()
    experimentResult
  }

  def results(experimentId: Int): ExperimentResultWithFormInputValues = {
    resultsMapping(experimentId)
  }
}
package io.perfix.manager

import com.google.inject.Inject
import io.perfix.common.ExperimentExecutor
import io.perfix.exceptions.InvalidStateException
import io.perfix.model._
import io.perfix.forms.Form
import io.perfix.store.ExperimentStore

import javax.inject.Singleton

@Singleton
class ExperimentManager @Inject()(datasetManager: DatasetManager,
                                  experimentStore: ExperimentStore,
                                  databaseConfigManager: DatabaseConfigManager) {

  def create(experimentParams: ExperimentParams): ExperimentId = {
    experimentStore
      .create(experimentParams)
      .experimentId
      .getOrElse(throw InvalidStateException("Invalid ExperimentParams"))
  }

  def get(experimentId: ExperimentId): ExperimentParams = {
    experimentStore.get(experimentId).getOrElse(throw InvalidStateException(s"Invalid ExperimentId ${experimentId}"))
  }

  def all: Seq[ExperimentParams] = {
    experimentStore.list()
  }

  def update(experimentId: ExperimentId, experimentParams: ExperimentParams): ExperimentParams = {
    experimentStore.update(experimentId, experimentParams)
    experimentParams
  }

  def executeExperiment(experimentId: ExperimentId): ExperimentParams = {
    val experimentParams = get(experimentId)
    val databaseConfigParams = databaseConfigManager.get(experimentParams.databaseConfigId)
    val dataset = datasetManager.get(experimentParams.datasetId).dataset
    val inputValues = databaseConfigParams.inputValues().getOrElse(Seq.empty).map { e =>
      e.inputName -> e.answer
    }.toMap
    val experimentExecutor = new ExperimentExecutor(databaseConfigParams.storeName, experimentParams, dataset)
    while (experimentExecutor.getFormSeriesEvaluator.hasNext) {
      val form = experimentExecutor.getFormSeriesEvaluator.next()
      val answerMapping = form.map { case (k, formInputType) =>
        val mappedValue = if (formInputType.isRequired) {
          Some(inputValues(k))
        } else {
          inputValues.get(k)
        }
        k -> mappedValue
      }
      experimentExecutor.getFormSeriesEvaluator.submit(Form.filteredAnswers(answerMapping))
    }

    val result = experimentExecutor.runExperiment()
    val updatedExperimentParams = experimentParams.copy(experimentResult = Some(result))
    experimentStore.update(experimentId, updatedExperimentParams)
    experimentExecutor.cleanUp()
    updatedExperimentParams
  }

  def delete(experimentId: ExperimentId): Unit = {
    experimentStore.delete(experimentId)
  }
}
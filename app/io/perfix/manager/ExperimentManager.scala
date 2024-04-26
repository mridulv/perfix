package io.perfix.manager

import com.google.inject.Inject
import io.perfix.common.ExperimentExecutor
import io.perfix.model._
import io.perfix.forms.Form

import javax.inject.Singleton
import scala.collection.mutable
import scala.util.Random

@Singleton
class ExperimentManager @Inject()(datasetManager: DatasetManager,
                                  databaseConfigManager: DatabaseConfigManager) {
  val mapping: mutable.Map[ExperimentId, ExperimentParams] = mutable.Map.empty[ExperimentId, ExperimentParams]

  def create(experimentParams: ExperimentParams): ExperimentId = {
    val id: Int = Random.nextInt()
    mapping.put(
      ExperimentId(id),
      experimentParams
    )
    ExperimentId(id)
  }

  def get(experimentId: ExperimentId): ExperimentParams = {
    mapping(experimentId)
  }

  def all: Seq[ExperimentParams] = {
    mapping.values.toSeq
  }

  def update(experimentId: ExperimentId, experimentParams: ExperimentParams): ExperimentParams = {
    mapping.put(experimentId, experimentParams)
    experimentParams
  }

  def executeExperiment(experimentId: ExperimentId): ExperimentParams = {
    val experimentParams = mapping(experimentId)
    val databaseConfigParams = databaseConfigManager.get(experimentParams.databaseConfigId)
    val dataset = datasetManager.get(experimentParams.datasetId)
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
    mapping.put(experimentId, experimentParams.copy(experimentResult = Some(result)))
    experimentExecutor.cleanUp()
    mapping(experimentId)
  }

  def delete(experimentId: ExperimentId): Unit = {
    mapping.remove(experimentId)
  }
}
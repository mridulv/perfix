package io.perfix.experiment

import io.perfix.launch.AWSCloudParams
import io.perfix.model.ExperimentParams
import io.perfix.question.experiment.{DataConfigurationForm, ExperimentParamsForm}
import io.perfix.question.{AWSCloudParamsForm, Form, FormSeries}
import io.perfix.stores.DataStore

class ExperimentFormSeries(experimentParams: ExperimentParams,
                           dataStore: DataStore) extends FormSeries {

  override val forms: Iterator[Form] = {
    val cloudParams = new AWSCloudParams
    val credentialsQuestion = new AWSCloudParamsForm(cloudParams)

    val launchQuestions = dataStore.launch(cloudParams) match {
      case Some(launchQuestion) => Iterator(credentialsQuestion) ++ Iterator(launchQuestion)
      case None => Iterator(credentialsQuestion)
    }

    val initialQuestions = Iterator(new DataConfigurationForm(experimentParams)) ++
      Iterator(new ExperimentParamsForm(experimentParams))

    val nextSet = dataStore.storeInputs(experimentParams.dataDescription).forms

    launchQuestions ++ initialQuestions ++ nextSet
  }
}

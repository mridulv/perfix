package io.perfix.experiment

import io.perfix.launch.AWSCloudParams
import io.perfix.model.ExperimentFormParams
import io.perfix.forms.experiment.{DataConfigurationForm, ExperimentParamsForm}
import io.perfix.forms.{AWSCloudParamsForm, Form, FormSeries}
import io.perfix.stores.DataStore

class ExperimentFormSeries(experimentParams: ExperimentFormParams,
                           dataStore: DataStore) extends FormSeries {

  override val forms: Iterator[Form] = {
    val cloudParams = new AWSCloudParams
    val credentialsForm = new AWSCloudParamsForm(cloudParams)

    val launchStoreForm = dataStore.launch(cloudParams) match {
      case Some(launchForm) => Iterator(credentialsForm) ++ Iterator(launchForm)
      case None => Iterator(credentialsForm)
    }

    val initForm = Iterator(new DataConfigurationForm(experimentParams)) ++
      Iterator(new ExperimentParamsForm(experimentParams))

    val nextSet = dataStore.storeInputs(experimentParams.dataDescription).forms

    launchStoreForm ++ initForm ++ nextSet
  }
}

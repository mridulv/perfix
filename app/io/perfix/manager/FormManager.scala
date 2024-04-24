package io.perfix.manager

import io.perfix.common.{ExperimentExecutor, FormSeriesEvaluator}
import io.perfix.forms.Form.FormInputName
import io.perfix.forms.{AWSCloudParamsForm, FormSeries}
import io.perfix.launch.AWSCloudParams
import io.perfix.model.{DatabaseConfigParams, DatasetParams, FormInputType, FormInputs}
import io.perfix.util.PeekingIterator

class FormManager(databaseConfigParams: DatabaseConfigParams) {

  private val iterator = {
    val dataStore = ExperimentExecutor.getDataStore(databaseConfigParams.storeName)
    val cloudParams = new AWSCloudParams
    val credentialsForm = new AWSCloudParamsForm(cloudParams)

    val launchForm = dataStore.launch(cloudParams) match {
      case Some(launchForm) => Iterator(credentialsForm) ++ Iterator(launchForm)
      case None => Iterator(credentialsForm)
    }

    val nextSet = dataStore.storeInputs(DatasetParams.empty()).forms
    val formSeries = new FormSeries {
      override val forms = launchForm ++ nextSet
    }

    new PeekingIterator[Map[FormInputName, FormInputType]](new FormSeriesEvaluator(formSeries))
  }

  def current: Option[FormInputs] = {
    iterator.peek().map(m => FormInputs(m))
  }

  def submit: Option[FormInputs] = {
    if (iterator.hasNext) {
      Some(FormInputs(iterator.next()))
    } else {
      None
    }
  }


}

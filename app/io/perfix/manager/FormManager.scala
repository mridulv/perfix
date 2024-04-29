package io.perfix.manager

import io.perfix.common.{ExperimentExecutor, FormSeriesEvaluator}
import io.perfix.forms.Form.FormInputName
import io.perfix.forms.{AWSCloudParamsForm, FormSeries}
import io.perfix.launch.AWSCloudParams
import io.perfix.model.{DatabaseConfigParams, DatasetParams, FormInputType, FormInputs}
import io.perfix.util.PeekingIterator

class FormManager(databaseConfigParams: DatabaseConfigParams) {

  private val iterator: PeekingIterator[Map[FormInputName, FormInputType]] = {
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

    val baseIterator = new FormSeriesEvaluator(formSeries)

    val updatedValueIterator = new Iterator[Map[FormInputName, FormInputType]] {
      override def hasNext: Boolean = baseIterator.hasNext

      override def next(): Map[FormInputName, FormInputType] = wrapValue(baseIterator.next())
    }

    new PeekingIterator[Map[FormInputName, FormInputType]](updatedValueIterator)
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

  private def wrapValue(form: Map[FormInputName, FormInputType]): Map[FormInputName, FormInputType] = {
    databaseConfigParams.formDetails match {
      case Some(formDetails) =>
        val formInputValueMap = formDetails.values.toMap
        form.map { case (k, v) =>
          val inputValue = formInputValueMap.get(k)
          k -> v.copy(defaultValue = inputValue)
        }
      case None =>
        form
    }
  }


}

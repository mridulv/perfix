package io.perfix.model

import io.perfix.common.ExperimentExecutor
import io.perfix.launch.AWSCloudParams
import io.perfix.forms.AWSCloudParamsForm
import play.api.libs.json.{Format, Json}

case class DatabaseConfigParams(databaseConfigId: Option[DatabaseConfigId] = None,
                                storeName: String,
                                formInputValues: Option[Seq[FormInputValue]] = None) {

  def formInputs: FormInputs = {
    val dataStore = ExperimentExecutor.getDataStore(storeName)
    val cloudParams = new AWSCloudParams
    val credentialsForm = new AWSCloudParamsForm(cloudParams)

    val launchForm = dataStore.launch(cloudParams) match {
      case Some(launchForm) => Iterator(credentialsForm) ++ Iterator(launchForm)
      case None => Iterator(credentialsForm)
    }

    val nextSet = dataStore.storeInputs(DataDescription()).forms

    FormInputs((launchForm ++ nextSet).toSeq.flatMap(_.mapping).toMap)
  }

}

object DatabaseConfigParams {
  implicit val DatabaseConfigParamsFormatter: Format[DatabaseConfigParams] = Json.format[DatabaseConfigParams]
}
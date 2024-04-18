package io.perfix.model

import io.perfix.common.ExperimentExecutor
import io.perfix.launch.AWSCloudParams
import io.perfix.forms.AWSCloudParamsForm
import play.api.libs.json.{Format, Json}

case class DatabaseConfigParams(databaseConfigId: Option[DatabaseConfigId] = None,
                                storeName: String,
                                perfixQuestionAnswers: Option[Seq[FormInputValue]] = None) {

  def questions: FormInputs = {
    val dataStore = ExperimentExecutor.getDataStore(storeName)
    val cloudParams = new AWSCloudParams
    val credentialsQuestion = new AWSCloudParamsForm(cloudParams)

    val launchQuestions = dataStore.launch(cloudParams) match {
      case Some(launchQuestion) => Iterator(credentialsQuestion) ++ Iterator(launchQuestion)
      case None => Iterator(credentialsQuestion)
    }

    val nextSet = dataStore.storeInputs(DataDescription()).forms

    FormInputs((launchQuestions ++ nextSet).toSeq.flatMap(_.mapping).toMap)
  }

}

object DatabaseConfigParams {
  implicit val DatabaseConfigParamsFormatter: Format[DatabaseConfigParams] = Json.format[DatabaseConfigParams]
}
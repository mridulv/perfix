package io.perfix.experiment

import io.perfix.launch.AWSCloudParams
import io.perfix.model.ExperimentParams
import io.perfix.question.experiment.{DataQuestions, ExperimentParamsForm}
import io.perfix.question.{AWSCloudParamsForm, Form, Questionnaire}
import io.perfix.stores.DataStore

class ExperimentQuestionnaire(experimentParams: ExperimentParams,
                              dataStore: DataStore) extends Questionnaire {

  override val questions: Iterator[Form] = {
    val cloudParams = new AWSCloudParams
    val credentialsQuestion = new AWSCloudParamsForm(cloudParams)

    val launchQuestions = dataStore.launch(cloudParams) match {
      case Some(launchQuestion) => Iterator(credentialsQuestion) ++ Iterator(launchQuestion)
      case None => Iterator(credentialsQuestion)
    }

    val initialQuestions = Iterator(new DataQuestions(experimentParams)) ++
      Iterator(new ExperimentParamsForm(experimentParams))

    val nextSet = dataStore.storeInputs(experimentParams.dataDescription).questions

    launchQuestions ++ initialQuestions ++ nextSet
  }
}

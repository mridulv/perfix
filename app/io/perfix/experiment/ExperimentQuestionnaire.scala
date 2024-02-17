package io.perfix.experiment

import io.perfix.launch.AWSCloudParams
import io.perfix.model.ExperimentParams
import io.perfix.question.experiment.{DataQuestions, ExperimentParamsQuestion}
import io.perfix.question.{AWSCloudParamsQuestion, Question, Questionnaire}
import io.perfix.stores.DataStore

class ExperimentQuestionnaire(experimentParams: ExperimentParams,
                              dataStore: DataStore) extends Questionnaire {

  override val questions: Iterator[Question] = {
    val cloudParams = new AWSCloudParams
    val credentialsQuestion = new AWSCloudParamsQuestion(cloudParams)

    val launchQuestions = dataStore.launch(cloudParams) match {
      case Some(launchQuestion) => Iterator(credentialsQuestion) ++ Iterator(launchQuestion)
      case None => Iterator(credentialsQuestion)
    }

    val initialQuestions = Iterator(new DataQuestions(experimentParams)) ++
      Iterator(new ExperimentParamsQuestion(experimentParams))

    val nextSet = dataStore.storeInputs(experimentParams.dataDescription).questions

    launchQuestions ++ initialQuestions ++ nextSet
  }
}

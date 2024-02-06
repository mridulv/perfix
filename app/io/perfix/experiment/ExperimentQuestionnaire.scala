package io.perfix.experiment

import io.perfix.launch.AWSCloudCredentials
import io.perfix.model.ExperimentParams
import io.perfix.question.experiment.{DataQuestions, ExperimentParamsQuestion}
import io.perfix.question.{AWSCredentialsQuestion, Question, Questionnaire}
import io.perfix.stores.DataStore

class ExperimentQuestionnaire(experimentParams: ExperimentParams,
                              dataStore: DataStore) extends Questionnaire {

  override val questions: Iterator[Question] = {
    val credentials = new AWSCloudCredentials
    val credentialsQuestion = new AWSCredentialsQuestion(credentials)

    val createQuestions = dataStore.create(credentials).map(q => Seq(credentialsQuestion, q)).getOrElse(Seq.empty)

    val initialQuestions = Iterator(new DataQuestions(experimentParams)) ++
      Iterator(new ExperimentParamsQuestion(experimentParams))

    val nextSet = dataStore.storeInputs(experimentParams.dataDescription).questions

    createQuestions.iterator ++ initialQuestions ++ nextSet
  }
}

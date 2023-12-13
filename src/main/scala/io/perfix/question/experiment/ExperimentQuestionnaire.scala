package io.perfix.question.experiment

import io.perfix.model.ExperimentParams
import io.perfix.question.{Question, Questionnaire}
import io.perfix.stores.DataStore

class ExperimentQuestionnaire(experimentParams: ExperimentParams,
                              dataStore: DataStore) extends Questionnaire {

  override protected val questions: Iterator[Question] = {
    val initialQuestions = Iterator(
      new DataQuestions(experimentParams),
      new ExperimentParamsQuestion(experimentParams)
    )

    val nextSet = dataStore.storeInputs(experimentParams.fakeData).questions

    initialQuestions ++ nextSet
  }
}

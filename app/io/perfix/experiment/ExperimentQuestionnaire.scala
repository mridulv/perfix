package io.perfix.experiment

import io.perfix.model.ExperimentParams
import io.perfix.question.experiment.{DataQuestions, ExperimentParamsQuestion}
import io.perfix.question.{Question, Questionnaire}
import io.perfix.stores.DataStore

class ExperimentQuestionnaire(experimentParams: ExperimentParams,
                              dataStore: DataStore) extends Questionnaire {

  override val questions: Iterator[Question] = {
    val initialQuestions = Iterator(
      new DataQuestions(experimentParams),
      new ExperimentParamsQuestion(experimentParams)
    )

    val nextSet = dataStore.storeInputs(experimentParams.dataDescription).questions

    initialQuestions ++ nextSet
  }
}

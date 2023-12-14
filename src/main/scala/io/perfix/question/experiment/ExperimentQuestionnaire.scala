package io.perfix.question.experiment

import io.perfix.context.QuestionExecutionContext
import io.perfix.model.ExperimentParams
import io.perfix.question.{Question, Questionnaire}
import io.perfix.stores.DataStore

class ExperimentQuestionnaire(experimentParams: ExperimentParams,
                              dataStore: DataStore,
                              questionExecutionContext: QuestionExecutionContext) extends Questionnaire {

  override protected val questions: Iterator[Question] = {
    val initialQuestions = Iterator(
      new DataQuestions(experimentParams, questionExecutionContext),
      new ExperimentParamsQuestion(experimentParams, questionExecutionContext)
    )

    val nextSet = dataStore.storeInputs(experimentParams.fakeData).questions

    initialQuestions ++ nextSet
  }
}

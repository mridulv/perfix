package io.perfix.experiment

import io.perfix.context.QuestionExecutionContext
import io.perfix.model.ExperimentParams
import io.perfix.query.PerfixQuery
import io.perfix.question.experiment.ExperimentQuestionnaire
import io.perfix.question.Questionnaire
import io.perfix.stores.DataStore

class SimplePerformanceExperiment(dataStore: DataStore,
                                  questionExecutionContext: QuestionExecutionContext) {

  private val experimentParams = new ExperimentParams

  def questions(): Questionnaire = {
    val questionnaire = new ExperimentQuestionnaire(experimentParams, dataStore, questionExecutionContext)
    questionnaire
  }

  def init(): Unit = {
    if (experimentParams.isDefined) {
      dataStore.connectAndInitialize()
    } else {
      throw new Exception("Parameters not defined correctly")
    }
  }

  def run(): Unit = {
    val query = dataStore.convertQuery(PerfixQuery())
    dataStore.putData()
    // Using experimentParams.concurrentQueriesOpt run experiment those many number of times using query
  }
}

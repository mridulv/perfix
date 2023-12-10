package io.perfix.experiment

import io.perfix.model.{DataDescription, ExperimentParams}
import io.perfix.query.PerfixQuery
import io.perfix.question.experiment.{DataQuestions, ExperimentParamsQuestion}
import io.perfix.question.{Question, Questionnaire}
import io.perfix.stores.DataStore

class SimplePerformanceExperiment(dataStore: DataStore) {

  val dataDescription = new DataDescription
  val experimentParams = new ExperimentParams

  def questions(): Questionnaire = {
    new Questionnaire {
      override protected val questions: Iterator[Question] = {
        Iterator(
          new DataQuestions(dataDescription),
          new ExperimentParamsQuestion(experimentParams)
        ) ++ dataStore.questions().questions
      }
    }
  }

  def init(): Unit = {
    if (dataDescription.isDefined) {
      dataStore.initialize()
    } else {
      throw new Exception("Parameters not defined correctly")
    }
  }


  def run(): Unit = {
    val query = dataStore.convertQuery(PerfixQuery())
    // Using experimentParams.concurrentQueriesOpt run experiment those many number of times using query
  }


}

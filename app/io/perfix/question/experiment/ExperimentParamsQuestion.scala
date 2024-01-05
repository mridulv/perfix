package io.perfix.question.experiment

import ExperimentParamsQuestion.CONCURRENT_QUERIES
import io.perfix.exceptions.ParamsAlreadyDefinedException
import io.perfix.model.{ColumnDescription, DataType, DoubleType, ExperimentParams, TextType}
import io.perfix.question.Question.QuestionLabel
import io.perfix.question.{Question, QuestionParams}

class ExperimentParamsQuestion(experimentParams: ExperimentParams) extends Question {

  override val mapping: Map[QuestionLabel, DataType] = Map(
    CONCURRENT_QUERIES -> DoubleType
  )

  override val storeQuestionParams: QuestionParams = experimentParams

  override def shouldAsk(): Boolean = {
    experimentParams.concurrentQueriesOpt.isEmpty
  }

  override def setAnswers(answers: Map[QuestionLabel, Any]): Unit = {
    import experimentParams._
    concurrentQueriesOpt match {
      case Some(_) => throw ParamsAlreadyDefinedException("DataDescription")
      case None => concurrentQueriesOpt = Some(answers(CONCURRENT_QUERIES).toString.toInt)
    }
  }

}

object ExperimentParamsQuestion {
  val CONCURRENT_QUERIES = "num_queries"
}
package io.perfix.question.experiment

import io.perfix.exceptions.ParamsAlreadyDefinedException
import io.perfix.model.{ColumnDescription, DoubleType, ExperimentParams, TextType}
import io.perfix.question.{Question, QuestionParams}

class ExperimentParamsQuestion(experimentParams: ExperimentParams) extends Question {
  private val CONCURRENT_QUERIES = "num_queries"

  override val storeQuestionParams: QuestionParams = experimentParams

  override def shouldAsk(): Boolean = {
    experimentParams.concurrentQueriesOpt.isEmpty
  }

  override def evaluateQuestions(): Unit = {
    import experimentParams._
    experimentParams.concurrentQueriesOpt match {
      case Some(_) => throw ParamsAlreadyDefinedException("DataDescription")
      case None =>
        val questionMapping = Map(
          CONCURRENT_QUERIES -> DoubleType
        )
        val answers = askQuestions(questionMapping)
        experimentParams.concurrentQueriesOpt = Some(answers(CONCURRENT_QUERIES).toString.toInt)
    }
  }

}

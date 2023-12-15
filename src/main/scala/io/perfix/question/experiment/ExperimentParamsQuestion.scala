package io.perfix.question.experiment

import io.perfix.context.QuestionExecutionContext
import io.perfix.exceptions.ParamsAlreadyDefinedException
import io.perfix.model.{ColumnDescription, DoubleType, ExperimentParams, TextType}
import io.perfix.question.experiment.ExperimentParamsQuestion.CONCURRENT_QUERIES
import io.perfix.question.{Question, QuestionParams}

class ExperimentParamsQuestion(experimentParams: ExperimentParams,
                               override val questionExecutionContext: QuestionExecutionContext) extends Question {

  override val storeQuestionParams: QuestionParams = experimentParams

  override def shouldAsk(): Boolean = {
    experimentParams.concurrentQueriesOpt.isEmpty
  }

  override def evaluateQuestion(): Unit = {
    import experimentParams._
    concurrentQueriesOpt match {
      case Some(_) => throw ParamsAlreadyDefinedException("DataDescription")
      case None =>
        val questionMapping = Map(
          CONCURRENT_QUERIES -> DoubleType
        )
        val answers = askQuestions(questionMapping)
        concurrentQueriesOpt = Some(answers(CONCURRENT_QUERIES).toString.toInt)
    }
  }

}

object ExperimentParamsQuestion {
  val CONCURRENT_QUERIES = "num_queries"
}
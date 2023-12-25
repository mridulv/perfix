package io.perfix.question.experiment

import io.perfix.context.QuestionExecutionContext
import io.perfix.exceptions.ParamsAlreadyDefinedException
import io.perfix.model.{ColumnDescription, DoubleType, ExperimentParams, StringType, TextType}
import io.perfix.question.experiment.DataQuestions._
import io.perfix.question.{Question, QuestionParams}

class DataQuestions(experimentParams: ExperimentParams,
                    override val questionExecutionContext: QuestionExecutionContext) extends Question {

  override val storeQuestionParams: QuestionParams = experimentParams

  override def shouldAsk(): Boolean = {
    import experimentParams._
    dataDescription.rowsOpt.isEmpty || dataDescription.columnsOpt.isEmpty
  }

  override def evaluateQuestion(): Unit = {
    import experimentParams._
    (dataDescription.rowsOpt, dataDescription.columnsOpt) match {
      case (None, None) =>
        val questionMapping = Map(
          ROWS -> DoubleType,
          COLUMNS -> StringType
        )
        val answers = askQuestions(questionMapping)
        dataDescription.rowsOpt = Some(answers(ROWS).toString.toInt)
        dataDescription.columnsOpt = Some(
          answers(COLUMNS).toString.split(",").map { e =>
            ColumnDescription(e, TextType)
          }
        )
        dataDescription.setData()
      case (_, _) => throw ParamsAlreadyDefinedException("DataDescription")
    }
  }
}

object DataQuestions {
  val ROWS = "rows"
  val COLUMNS = "columns"
}
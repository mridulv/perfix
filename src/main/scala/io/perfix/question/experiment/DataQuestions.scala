package io.perfix.question.experiment

import io.perfix.context.QuestionExecutionContext
import io.perfix.exceptions.ParamsAlreadyDefinedException
import io.perfix.model.{ColumnDescription, DataDescription, DoubleType, ExperimentParams, StringType, TextType}
import io.perfix.question.{Question, QuestionParams}

class DataQuestions(experimentParams: ExperimentParams,
                    override val questionExecutionContext: QuestionExecutionContext) extends Question {
  private val ROWS = "rows"
  private val COLUMNS = "columns"

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
            ColumnDescription(e.split(":").head, TextType)
          }
        )
      case (_, _) => throw ParamsAlreadyDefinedException("DataDescription")
    }
  }
}

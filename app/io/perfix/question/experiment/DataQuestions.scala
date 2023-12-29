package io.perfix.question.experiment

import DataQuestions._
import io.perfix.context.QuestionExecutionContext
import io.perfix.exceptions.ParamsAlreadyDefinedException
import io.perfix.model.{ColumnDescription, DataType, DoubleType, ExperimentParams, StringType, TextType}
import io.perfix.question.Question.QuestionLabel
import io.perfix.question.{Question, QuestionParams}

class DataQuestions(experimentParams: ExperimentParams,
                    override val questionExecutionContext: QuestionExecutionContext) extends Question {

  override val mapping: Map[QuestionLabel, DataType] = Map(
    ROWS -> DoubleType,
    COLUMNS -> StringType
  )

  override val storeQuestionParams: QuestionParams = experimentParams

  override def shouldAsk(): Boolean = {
    import experimentParams._
    dataDescription.rowsOpt.isEmpty || dataDescription.columnsOpt.isEmpty
  }

  override def evaluateQuestion(): Unit = {
    import experimentParams._
    (dataDescription.rowsOpt, dataDescription.columnsOpt) match {
      case (None, None) =>
        val answers = askQuestions
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
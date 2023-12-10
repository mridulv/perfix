package io.perfix.question.experiment

import io.perfix.{DoubleType, StringType}
import io.perfix.exceptions.ParamsAlreadyDefinedException
import io.perfix.model.{ColumnDescription, DataDescription, TextType}
import io.perfix.question.{Question, QuestionParams}

class DataQuestions(dataDescription: DataDescription) extends Question {
  private val ROWS = "rows"
  private val COLUMNS = "columns"

  override val storeQuestionParams: QuestionParams = dataDescription

  override def shouldAsk(): Boolean = {
    dataDescription.rowsOpt.isEmpty || dataDescription.columnsOpt.isEmpty
  }

  override def evaluateQuestions(): Unit = {
    import dataDescription._
    (dataDescription.rowsOpt, dataDescription.columnsOpt) match {
      case (None, None) =>
        val questionMapping = Map(
          ROWS -> DoubleType,
          COLUMNS -> StringType
        )
        val answers = askQuestions(questionMapping)
        rowsOpt = Some(answers(ROWS).toString.toInt)
        columnsOpt = Some(
          answers(COLUMNS).toString.split(",").map { e =>
            ColumnDescription(e.split(":").head, TextType)
          }
        )
      case (_, _) => throw ParamsAlreadyDefinedException("DataDescription")
    }
  }
}

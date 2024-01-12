package io.perfix.question.experiment

import DataQuestions._
import io.perfix.exceptions.ParamsAlreadyDefinedException
import io.perfix.model.{ColumnDescription, DataType, DoubleType, ExperimentParams, QuestionType, StringType, TextType}
import io.perfix.question.Question.QuestionLabel
import io.perfix.question.{Question, QuestionParams}
import play.api.libs.json.Json

class DataQuestions(experimentParams: ExperimentParams) extends Question {

  override val mapping: Map[QuestionLabel, QuestionType] = Map(
    ROWS -> QuestionType(DoubleType),
    COLUMNS -> QuestionType(StringType)
  )

  override val storeQuestionParams: QuestionParams = experimentParams

  override def shouldAsk(): Boolean = {
    import experimentParams._
    dataDescription.rowsOpt.isEmpty || dataDescription.columnsOpt.isEmpty
  }

  override def setAnswers(answers: Map[QuestionLabel, Any]): Unit = {
    import experimentParams._
    (dataDescription.rowsOpt, dataDescription.columnsOpt) match {
      case (None, None) =>
        dataDescription.rowsOpt = Some(answers(ROWS).toString.toInt)
        dataDescription.columnsOpt = Some(Json.parse(answers(COLUMNS).toString).as[Seq[ColumnDescription]])
        dataDescription.setData()
      case (_, _) => throw ParamsAlreadyDefinedException("DataDescription")
    }
  }
}

object DataQuestions {
  val ROWS = "rows"
  val COLUMNS = "columns"
}
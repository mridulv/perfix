package io.perfix.question.experiment

import DataQuestions._
import io.perfix.exceptions.{InvalidQuestionParameterExceptions, ParamsAlreadyDefinedException}
import io.perfix.model.{ColumnDescription, DoubleType, ExperimentParams, QuestionType, StringType}
import io.perfix.question.Form.FormInputName
import io.perfix.question.{Form, FormParams}
import play.api.libs.json.Json

class DataQuestions(experimentParams: ExperimentParams) extends Form {

  override val mapping: Map[FormInputName, QuestionType] = Map(
    ROWS -> QuestionType(DoubleType),
    COLUMNS -> QuestionType(StringType)
  )

  override val storeQuestionParams: FormParams = experimentParams

  override def shouldAsk(): Boolean = {
    import experimentParams._
    dataDescription.rowsOpt.isEmpty || dataDescription.columnsOpt.isEmpty
  }

  override def setAnswers(answers: Map[FormInputName, Any]): Unit = {
    import experimentParams._
    (dataDescription.rowsOpt, dataDescription.columnsOpt) match {
      case (None, None) =>
        val columnDescriptions = Json.parse(answers(COLUMNS).toString).as[Seq[ColumnDescription]]
        dataDescription.rowsOpt = Some(answers(ROWS).toString.toInt)
        if (columnDescriptions.toSeq.forall(_.isValid)) {
          dataDescription.columnsOpt = Some(columnDescriptions)
        } else {
          throw InvalidQuestionParameterExceptions("Columns")
        }
        dataDescription.setData()
      case (_, _) => throw ParamsAlreadyDefinedException("DataDescription")
    }
  }
}

object DataQuestions {
  val ROWS = "rows"
  val COLUMNS = "columns"
}
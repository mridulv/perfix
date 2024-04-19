package io.perfix.forms.experiment

import DataConfigurationForm._
import io.perfix.exceptions.{InvalidFormParameterExceptions, ParamsAlreadyDefinedException}
import io.perfix.model.{ColumnDescription, DoubleType, ExperimentFormParams, FormInputType, StringType}
import io.perfix.forms.Form.FormInputName
import io.perfix.forms.{Form, FormParams}
import play.api.libs.json.Json

class DataConfigurationForm(experimentParams: ExperimentFormParams) extends Form {

  override val mapping: Map[FormInputName, FormInputType] = Map(
    ROWS -> FormInputType(DoubleType),
    COLUMNS -> FormInputType(StringType)
  )

  override val formParams: FormParams = experimentParams

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
          throw InvalidFormParameterExceptions("Columns")
        }
        dataDescription.setData()
      case (_, _) => throw ParamsAlreadyDefinedException("DataDescription")
    }
  }
}

object DataConfigurationForm {
  val ROWS = "rows"
  val COLUMNS = "columns"
}
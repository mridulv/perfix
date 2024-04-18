package io.perfix.model

import play.api.libs.json.{Format, Json}

case class ExperimentResultWithFormInputValues(perfixExperimentResult: Option[ExperimentResult] = None,
                                               formInputValues: FormInputValues) {

  def addFormInputValue(formInputValue: FormInputValue): ExperimentResultWithFormInputValues = {
    this.copy(formInputValues = this.formInputValues.addFormInputValue(formInputValue))
  }

  def addPerfixQuestionAnswers(formInputValues: Seq[FormInputValue]): ExperimentResultWithFormInputValues = {
    this.copy(formInputValues = this.formInputValues.addFormInputValues(formInputValues))
  }

  def addPerfixExperimentResult(perfixExperimentResult: ExperimentResult): ExperimentResultWithFormInputValues = {
    this.copy(perfixExperimentResult = Some(perfixExperimentResult))
  }

}

object ExperimentResultWithFormInputValues {
  implicit val ExperimentResultWithFormInputValuesFormatter: Format[ExperimentResultWithFormInputValues] = Json.format[ExperimentResultWithFormInputValues]

  def empty: ExperimentResultWithFormInputValues = {
    ExperimentResultWithFormInputValues(None, FormInputValues.empty)
  }
}
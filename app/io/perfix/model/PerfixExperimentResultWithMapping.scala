package io.perfix.model

import play.api.libs.json.{Format, Json}

case class PerfixExperimentResultWithMapping(perfixExperimentResult: Option[ExperimentResult] = None,
                                             perfixQuestionAnswers: FormInputValues) {

  def addPerfixQuestionAnswer(perfixQuestionAnswer: FormInputValue): PerfixExperimentResultWithMapping = {
    this.copy(perfixQuestionAnswers = this.perfixQuestionAnswers.addPerfixQuestionAnswer(perfixQuestionAnswer))
  }

  def addPerfixQuestionAnswers(perfixQuestionAnswers: Seq[FormInputValue]): PerfixExperimentResultWithMapping = {
    this.copy(perfixQuestionAnswers = this.perfixQuestionAnswers.addPerfixQuestionAnswers(perfixQuestionAnswers))
  }

  def addPerfixExperimentResult(perfixExperimentResult: ExperimentResult): PerfixExperimentResultWithMapping = {
    this.copy(perfixExperimentResult = Some(perfixExperimentResult))
  }

}

object PerfixExperimentResultWithMapping {
  implicit val PerfixExperimentResultWithMappingFormatter: Format[PerfixExperimentResultWithMapping] = Json.format[PerfixExperimentResultWithMapping]

  def empty: PerfixExperimentResultWithMapping = {
    PerfixExperimentResultWithMapping(None, FormInputValues.empty)
  }
}
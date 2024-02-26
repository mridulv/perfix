package io.perfix.model

import play.api.libs.json.{Format, Json}

case class PerfixExperimentResultWithMapping(perfixExperimentResult: Option[PerfixExperimentResult] = None,
                                             perfixQuestionAnswers: PerfixQuestionAnswers) {

  def addPerfixQuestionAnswer(perfixQuestionAnswer: PerfixQuestionAnswer): PerfixExperimentResultWithMapping = {
    this.copy(perfixQuestionAnswers = this.perfixQuestionAnswers.addPerfixQuestionAnswer(perfixQuestionAnswer))
  }

  def addPerfixQuestionAnswers(perfixQuestionAnswers: Seq[PerfixQuestionAnswer]): PerfixExperimentResultWithMapping = {
    this.copy(perfixQuestionAnswers = this.perfixQuestionAnswers.addPerfixQuestionAnswers(perfixQuestionAnswers))
  }

  def addPerfixExperimentResult(perfixExperimentResult: PerfixExperimentResult): PerfixExperimentResultWithMapping = {
    this.copy(perfixExperimentResult = Some(perfixExperimentResult))
  }

}

object PerfixExperimentResultWithMapping {
  implicit val PerfixExperimentResultWithMappingFormatter: Format[PerfixExperimentResultWithMapping] = Json.format[PerfixExperimentResultWithMapping]

  def empty: PerfixExperimentResultWithMapping = {
    PerfixExperimentResultWithMapping(None, PerfixQuestionAnswers.empty)
  }
}
package io.perfix.controllers

import controllers.AssetsFinder
import io.perfix.manager.ExperimentManager
import io.perfix.model.PerfixQuestionAnswers
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents, Results}

import javax.inject.{Inject, Singleton}

@Singleton
class PerfixQuestionnaireController @Inject()(cc: ControllerComponents,
                                              perfixManager: ExperimentManager)(implicit assetsFinder: AssetsFinder)
  extends AbstractController(cc) {

  def questionnaire(storeName: String) = Action {
    Results.Ok(Json.toJson(perfixManager.startQuestionnaire(storeName)))
  }

  def questions(experimentId: Int) = Action {
    Results.Ok(Json.toJson(perfixManager.nextQuestion(experimentId)))
  }

  def submitQuestion(experimentId: Int) = Action(parse.json) { request =>
    val perfixQuestionAnswers = request.body.as[PerfixQuestionAnswers]
    perfixManager.submitQuestionAnswer(experimentId, perfixQuestionAnswers)
    Results.Ok
  }

  def startExperiment(experimentId: Int) = Action {
    Results.Ok(Json.toJson(perfixManager.startExperiment(experimentId)))
  }

}

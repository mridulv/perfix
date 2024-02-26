package io.perfix.controllers

import controllers.AssetsFinder
import io.perfix.common.PerfixManager
import io.perfix.model.PerfixQuestion._
import io.perfix.model.PerfixQuestionAnswers
import play.api.libs.json.Json
import play.api.mvc._

import javax.inject._

@Singleton
class PerfixController @Inject()(cc: ControllerComponents,
                                 perfixManager: PerfixManager)(implicit assetsFinder: AssetsFinder)
  extends AbstractController(cc) {

  def questionnaire(storeName: String) = Action {
    Results.Ok(Json.toJson(perfixManager.startQuestionnaire(storeName)))
  }

  def startExperiment(questionnaireId: Int) = Action {
    Results.Ok(Json.toJson(perfixManager.startExperiment(questionnaireId)))
  }

  def questions(questionnaireId: Int) = Action {
    Results.Ok(Json.toJson(perfixManager.nextQuestion(questionnaireId)))
  }

  def submitQuestion(questionnaireId: Int) = Action(parse.json) { request =>
    val perfixQuestionAnswers = request.body.as[PerfixQuestionAnswers]
    perfixManager.submitQuestionAnswer(questionnaireId, perfixQuestionAnswers)
    Results.Ok
  }

  def executeExperiment(storeName: String) = Action(parse.json) { request =>
    val perfixQuestionAnswers = request.body.as[PerfixQuestionAnswers]
    Results.Ok(Json.toJson(perfixManager.executeExperiment(storeName, perfixQuestionAnswers)))
  }

  def experimentResults(experimentId: Int) = Action { request =>
    Results.Ok(Json.toJson(perfixManager.results(experimentId)).toString())
  }

}

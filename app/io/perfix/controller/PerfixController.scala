package io.perfix.controller

import controllers.AssetsFinder
import play.api.libs.json.Json
import play.api.mvc._

import javax.inject._
import io.perfix.model.PerfixQuestion._
import io.perfix.model.{PerfixQuestionAnswer, PerfixQuestionAnswers}

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

}

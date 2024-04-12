package io.perfix.controllers

import controllers.AssetsFinder
import io.perfix.common.ExperimentManager
import io.perfix.model.{ExperimentRunParams, PerfixQuestionAnswers}
import play.api.libs.json.Json
import play.api.mvc._

import javax.inject._

@Singleton
class ExperimentController @Inject()(cc: ControllerComponents,
                                     perfixManager: ExperimentManager)(implicit assetsFinder: AssetsFinder)
  extends AbstractController(cc) {

  def executeExperiment(storeName: String) = Action(parse.json) { request =>
    val perfixQuestionAnswers = request.body.as[PerfixQuestionAnswers]
    Results.Ok(Json.toJson(perfixManager.executeExperiment(storeName, perfixQuestionAnswers)))
  }

  def repeatExperiment(experimentId: Int) = Action(parse.json) { request =>
    val experimentRunParams = request.body.as[ExperimentRunParams]
    Results.Ok(Json.toJson(perfixManager.repeatExperiment(experimentId, experimentRunParams)))
  }

  def experimentResults(experimentId: Int) = Action { request =>
    Results.Ok(Json.toJson(perfixManager.results(experimentId)).toString())
  }

}

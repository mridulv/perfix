package io.perfix.controllers

import controllers.AssetsFinder
import io.perfix.common.PerfixManager
import io.perfix.model.PerfixQuestion._
import io.perfix.model.PerfixQuestionAnswers
import play.api.libs.json.Json
import play.api.mvc._

import javax.inject._

@Singleton
class PerfixExperimentController @Inject()(cc: ControllerComponents,
                                           perfixManager: PerfixManager)(implicit assetsFinder: AssetsFinder)
  extends AbstractController(cc) {

  def executeExperiment(storeName: String) = Action(parse.json) { request =>
    val perfixQuestionAnswers = request.body.as[PerfixQuestionAnswers]
    Results.Ok(Json.toJson(perfixManager.executeExperiment(storeName, perfixQuestionAnswers)))
  }

  def repeatExperiment(experimentId: Int) = Action { request =>
    perfixManager.repeatExperiment(experimentId)
    Results.Ok
  }

  def experimentResults(experimentId: Int) = Action { request =>
    Results.Ok(Json.toJson(perfixManager.results(experimentId)).toString())
  }

}

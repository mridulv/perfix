package io.perfix.controllers

import controllers.AssetsFinder
import io.perfix.manager.ExperimentManager
import io.perfix.model.{DatabaseConfigId, ExperimentId, ExperimentParams}
import play.api.libs.json.Json
import play.api.mvc._

import javax.inject._

@Singleton
class ExperimentController @Inject()(cc: ControllerComponents,
                                     experimentManager: ExperimentManager)(implicit assetsFinder: AssetsFinder)
  extends AbstractController(cc) {

  def create = Action(parse.json) { request =>
    val experimentParams = request.body.as[ExperimentParams]
    Results.Ok(Json.toJson(experimentManager.create(experimentParams)))
  }

  def get(experimentId: Int) = Action { request =>
    Results.Ok(Json.toJson(experimentManager.get(ExperimentId(experimentId))))
  }

  def update(experimentId: Int) = Action(parse.json) { request =>
    val experimentParams = request.body.as[ExperimentParams]
    Results.Ok(Json.toJson(experimentManager.update(ExperimentId(experimentId), experimentParams)))
  }

  def all = Action { request =>
    Results.Ok(Json.toJson(experimentManager.all))
  }

  def executeExperiment(experimentId: Int) = Action(parse.json) { request =>
    Results.Ok(Json.toJson(experimentManager.executeExperiment(ExperimentId(experimentId))))
  }

  def delete(experimentId: Int) = Action { request =>
    experimentManager.delete(ExperimentId(experimentId))
    Results.Ok
  }
}

package io.perfix.controllers

import controllers.AssetsFinder
import io.perfix.auth.AuthenticationAction
import io.perfix.manager.ExperimentManager
import io.perfix.model.EntityFilter
import io.perfix.model.experiment.{ExperimentId, ExperimentParams}
import org.pac4j.core.profile.UserProfile
import org.pac4j.play.scala.{Security, SecurityComponents}
import play.api.libs.json.Json
import play.api.mvc._

import javax.inject._

@Singleton
class ExperimentController @Inject()(val controllerComponents: SecurityComponents,
                                     authenticationAction: AuthenticationAction,
                                     experimentManager: ExperimentManager)(implicit assetsFinder: AssetsFinder)
  extends Security[UserProfile] {

  def create = authenticationAction(parse.json) { request =>
      val experimentParams = request.body.as[ExperimentParams]
      Results.Ok(Json.toJson(experimentManager.create(experimentParams)))
  }

  def get(experimentId: Int) = authenticationAction { request =>
      Results.Ok(Json.toJson(experimentManager.get(ExperimentId(experimentId))))
  }

  def update(experimentId: Int) = authenticationAction(parse.json) { request =>
      val experimentParams = request.body.as[ExperimentParams]
      Results.Ok(Json.toJson(experimentManager.update(ExperimentId(experimentId), experimentParams)))
  }

  def all = authenticationAction(parse.json) { request =>
      val filters = request.body.as[Seq[EntityFilter]]
      Results.Ok(Json.toJson(experimentManager.all(filters)))
  }

  def executeExperiment(experimentId: Int) = authenticationAction(parse.json) { request =>
      Results.Ok(Json.toJson(experimentManager.executeExperiment(ExperimentId(experimentId))))
  }

  def delete(experimentId: Int) = authenticationAction { request =>
      experimentManager.delete(ExperimentId(experimentId))
      Results.Ok
  }
}

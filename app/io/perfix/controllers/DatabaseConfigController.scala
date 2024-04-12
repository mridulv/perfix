package io.perfix.controllers

import com.google.inject.{Inject, Singleton}
import io.perfix.manager.DatabaseConfigManager
import io.perfix.model.{DatabaseConfigParams, DatabaseConfigId}
import io.perfix.model.DatabaseConfigId._
import io.perfix.model.DatabaseConfigParams._
import play.api.libs.json.Json
import play.api.mvc.{BaseController, ControllerComponents, Results}

@Singleton
class DatabaseConfigController @Inject()(val controllerComponents: ControllerComponents,
                                         databaseConfigManager: DatabaseConfigManager) extends BaseController {
  def create = Action(parse.json) { request =>
    val databaseConfigParams = request.body.as[DatabaseConfigParams]
    Results.Ok(Json.toJson(databaseConfigManager.create(databaseConfigParams)))
  }

  def get(databaseConfigId: Int) = Action { request =>
    Results.Ok(Json.toJson(databaseConfigManager.get(DatabaseConfigId(databaseConfigId))))
  }

  def all = Action { request =>
    Results.Ok(Json.toJson(databaseConfigManager.all()))
  }
}
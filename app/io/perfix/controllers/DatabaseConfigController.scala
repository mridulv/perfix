package io.perfix.controllers

import com.google.inject.{Inject, Singleton}
import io.perfix.manager.DatabaseConfigManager
import io.perfix.model.{DatabaseConfigId, DatabaseConfigParams, EntityFilter}
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

  def update(databaseConfigId: Int) = Action(parse.json) { request =>
    val databaseConfigParams = request.body.as[DatabaseConfigParams]
    Results.Ok(
      Json.toJson(
        databaseConfigManager.update(
          DatabaseConfigId(databaseConfigId),
          databaseConfigParams
        )
      )
    )
  }

  def all = Action { request =>
    Results.Ok(Json.toJson(databaseConfigManager.all(Seq.empty)))
  }

  def filteredDatabaseConfigs = Action(parse.json) { request =>
    val filters = request.body.as[Seq[EntityFilter]]
    Results.Ok(Json.toJson(databaseConfigManager.all(filters)))
  }

  def delete(databaseConfigId: Int) = Action { request =>
    databaseConfigManager.delete(DatabaseConfigId(databaseConfigId))
    Results.Ok
  }
}

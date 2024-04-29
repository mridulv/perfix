package io.perfix.controllers

import com.google.inject.{Inject, Singleton}
import io.perfix.manager.DatabaseConfigManager
import io.perfix.model.{DatabaseConfigId, DatabaseConfigParams, FormInputValues}
import io.perfix.model.DatabaseConfigId._
import io.perfix.model.DatabaseConfigParams._
import play.api.libs.json.Json
import play.api.mvc.{BaseController, ControllerComponents, Results}
import io.perfix.model.FormStatus.FormStatusFormat

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

  def getForm(databaseConfigId: Int) = Action { request =>
    Results.Ok(Json.toJson(databaseConfigManager.currentForm(DatabaseConfigId(databaseConfigId))))
  }

  def submitForm(databaseConfigId: Int) = Action(parse.json) { request =>
    val formInputValues = request.body.as[FormInputValues]
    Results.Ok(databaseConfigManager.submitForm(DatabaseConfigId(databaseConfigId), formInputValues).map(_.toString).getOrElse(""))
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
    Results.Ok(Json.toJson(databaseConfigManager.all()))
  }

  def delete(databaseConfigId: Int) = Action { request =>
    databaseConfigManager.delete(DatabaseConfigId(databaseConfigId))
    Results.Ok
  }
}

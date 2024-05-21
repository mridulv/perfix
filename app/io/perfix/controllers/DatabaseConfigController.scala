package io.perfix.controllers

import com.google.inject.{Inject, Singleton}
import io.perfix.manager.DatabaseConfigManager
import io.perfix.model.{DatabaseConfigId, DatabaseConfigParams, EntityFilter}
import io.perfix.model.DatabaseConfigId._
import io.perfix.model.DatabaseConfigParams._
import org.pac4j.core.profile.UserProfile
import org.pac4j.play.scala.{Security, SecurityComponents}
import play.api.libs.json.Json
import play.api.mvc.{BaseController, ControllerComponents, Results}

@Singleton
class DatabaseConfigController @Inject()(val controllerComponents: SecurityComponents,
                                         databaseConfigManager: DatabaseConfigManager) extends Security[UserProfile] {
  def create = Secure("Google2Client") {
    Action(parse.json) { request =>
      val databaseConfigParams = request.body.as[DatabaseConfigParams]
      Results.Ok(Json.toJson(databaseConfigManager.create(databaseConfigParams)))
    }
  }

  def get(databaseConfigId: Int) = Secure("Google2Client") {
    Action { request =>
      Results.Ok(Json.toJson(databaseConfigManager.get(DatabaseConfigId(databaseConfigId))))
    }
  }

  def update(databaseConfigId: Int) = Secure("Google2Client"){
    Action(parse.json) { request =>
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
  }

  def all = Secure("Google2Client") {
    Action(parse.json) { request =>
      val filters = request.body.as[Seq[EntityFilter]]
      Results.Ok(Json.toJson(databaseConfigManager.all(filters)))
    }
  }

  def delete(databaseConfigId: Int) = Secure("Google2Client") {
    Action { request =>
      databaseConfigManager.delete(DatabaseConfigId(databaseConfigId))
      Results.Ok
    }
  }
}

package io.perfix.controllers

import com.google.inject.{Inject, Singleton}
import io.perfix.auth.AuthenticationAction
import io.perfix.manager.DatabaseConfigManager
import io.perfix.model.api.{DatabaseConfigId, DatabaseConfigParams}
import io.perfix.model.api.DatabaseConfigId._
import io.perfix.model.api.DatabaseConfigParams._
import io.perfix.model.EntityFilter
import org.pac4j.core.profile.UserProfile
import org.pac4j.play.scala.{Security, SecurityComponents}
import play.api.libs.json.Json
import play.api.mvc.Results

@Singleton
class DatabaseConfigController @Inject()(val controllerComponents: SecurityComponents,
                                         authenticationAction: AuthenticationAction,
                                         databaseConfigManager: DatabaseConfigManager) extends Security[UserProfile] {
  def create = authenticationAction(parse.json) { request =>
      val databaseConfigParams = request.body.as[DatabaseConfigParams]
      Results.Ok(Json.toJson(databaseConfigManager.create(databaseConfigParams)))
  }

  def get(databaseConfigId: Int) = authenticationAction { request =>
      Results.Ok(Json.toJson(databaseConfigManager.get(DatabaseConfigId(databaseConfigId))))
  }

  def update(databaseConfigId: Int) = authenticationAction(parse.json) { request =>
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

  def all = authenticationAction(parse.json) { request =>
      val filters = request.body.as[Seq[EntityFilter]]
      Results.Ok(Json.toJson(databaseConfigManager.all(filters)))
  }

  def delete(databaseConfigId: Int) = authenticationAction { request =>
      databaseConfigManager.delete(DatabaseConfigId(databaseConfigId))
      Results.Ok
  }
}

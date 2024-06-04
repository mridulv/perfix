package io.perfix.controllers

import com.google.inject.{Inject, Singleton}
import io.perfix.auth.AuthenticationAction
import io.perfix.manager.ConfigManager
import org.pac4j.core.profile.UserProfile
import org.pac4j.play.scala.{Security, SecurityComponents}
import play.api.mvc.Results

@Singleton
class ConfigController @Inject()(val controllerComponents: SecurityComponents,
                                 authenticationAction: AuthenticationAction,
                                 configManager: ConfigManager) extends Security[UserProfile] {


  def databases = authenticationAction { request =>
    Results.Ok
  }

  def inputs(databaseType: String) = authenticationAction { request =>
    Results.Ok
  }

}

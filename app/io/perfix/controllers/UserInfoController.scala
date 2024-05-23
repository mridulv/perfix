package io.perfix.controllers

import com.google.inject.{Inject, Singleton}
import io.perfix.model.{DatasetId, DatasetParams, UserInfo}
import org.pac4j.core.context.session.SessionStore
import org.pac4j.core.profile.{ProfileManager, UserProfile}
import org.pac4j.play.PlayWebContext
import org.pac4j.play.scala.{DefaultSecurityComponents, SecureAction, Security, SecurityComponents}
import play.api.libs.json.Json
import play.api.mvc._

import scala.jdk.CollectionConverters._

@Singleton
class UserInfoController @Inject()(val controllerComponents: SecurityComponents,
                                   sessionStore: SessionStore) extends Security[UserProfile] {

  def login: Action[AnyContent] = Secure("Google2Client") {
    Action(parse.json) { request =>
      Ok
    }
  }

  def me: Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    val webContext = new PlayWebContext(request)
    val profileManager = new ProfileManager(webContext, sessionStore)
    val profiles = profileManager.getProfiles.asScala

    if (profiles.nonEmpty) {
      val name = profiles.map(_.getAttribute("name").toString).headOption
      val email = profiles.map(_.getAttribute("email").toString).headOption

      val userInfo = UserInfo(name, email)

      Ok(Json.toJson(userInfo))
    } else {
      Ok(Json.parse("{}"))
    }
  }
}

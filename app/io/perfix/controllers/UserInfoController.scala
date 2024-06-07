package io.perfix.controllers

import com.google.inject.{Inject, Singleton}
import io.perfix.model.UserInfo
import org.pac4j.core.context.session.SessionStore
import org.pac4j.core.profile.{ProfileManager, UserProfile}
import org.pac4j.play.PlayWebContext
import org.pac4j.play.scala.{Security, SecurityComponents}
import play.api.Configuration
import play.api.libs.json.Json
import play.api.mvc._

import scala.jdk.CollectionConverters._

@Singleton
class UserInfoController @Inject()(val controllerComponents: SecurityComponents,
                                   sessionStore: SessionStore,
                                   configuration: Configuration) extends Security[UserProfile] {

  private val BASE_URL: String = configuration.get[String]("pac4j.googleClient.baseUrl")

  def login = Secure("Google2Client") {
    Action { implicit request =>
      Redirect(BASE_URL)
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

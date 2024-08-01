package io.perfix.controllers

import com.google.inject.{Inject, Singleton}
import io.perfix.auth.CustomPlayCookieSessionStore
import io.perfix.auth.CustomPlayCookieSessionStore.PAC4J_SESSION_TIMEOUT
import io.perfix.model.UserInfo
import org.pac4j.core.context.session.SessionStore
import org.pac4j.core.profile.{ProfileManager, UserProfile}
import org.pac4j.play.PlayWebContext
import org.pac4j.play.scala.{Security, SecurityComponents}
import play.api.Configuration
import play.api.libs.json.Json
import play.api.mvc._

import scala.jdk.CollectionConverters._
import scala.util.Try

@Singleton
class UserInfoController @Inject()(val controllerComponents: SecurityComponents,
                                   sessionStore: SessionStore,
                                   configuration: Configuration) extends Security[UserProfile] {

  private val APP_URL: String = configuration.get[String]("app.baseUrl")

  def login = Secure("Google2Client") {
    Action { implicit request =>
      Redirect(APP_URL)
    }
  }

  def me: Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    val webContext = new PlayWebContext(request)
    val profileManager = new ProfileManager(webContext, sessionStore)
    val profiles = profileManager.getProfiles.asScala
    val name = profiles.map(_.getAttribute("name").toString).headOption
    val email = profiles.map(_.getAttribute("email").toString).headOption
    val sessionTimeout = Try(sessionStore.get(webContext, PAC4J_SESSION_TIMEOUT).get().asInstanceOf[Long]).toOption.getOrElse(0L)
    println("SessionTimeout is: " + sessionTimeout)

    if (profiles.nonEmpty && name.isDefined && email.isDefined && System.currentTimeMillis() < sessionTimeout) {
      val userInfo = UserInfo(name.get, email.get)
      Ok(Json.toJson(userInfo))
    } else {
      Ok(Json.parse("{}"))
    }
  }
}

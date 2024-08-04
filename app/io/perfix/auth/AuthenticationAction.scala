package io.perfix.auth

import com.google.inject.Inject
import io.perfix.auth.CustomPlayCookieSessionStore.PAC4J_SESSION_TIMEOUT
import io.perfix.model.UserInfo
import org.pac4j.core.context.session.SessionStore
import org.pac4j.core.profile.ProfileManager
import org.pac4j.play.PlayWebContext
import play.api.Logging
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters._
import scala.util.Try

class AuthenticationAction @Inject() (parser: BodyParsers.Default,
                                      sessionStore: SessionStore)(implicit ec: ExecutionContext)
  extends ActionBuilderImpl(parser)
    with Logging {
  override def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) = {
    val webContext = new PlayWebContext(request)
    val profileManager = new ProfileManager(webContext, sessionStore)
    val profiles = profileManager.getProfiles.asScala
    val name = profiles.map(_.getAttribute("name").toString).headOption
    val email = profiles.map(_.getAttribute("email").toString).headOption
    val sessionTimeout = Try(sessionStore.get(webContext, PAC4J_SESSION_TIMEOUT).get().asInstanceOf[Long]).toOption.getOrElse(0L)

    if (profiles.nonEmpty && name.isDefined && email.isDefined && System.currentTimeMillis() < sessionTimeout) {
      val userInfo = UserInfo(name.get, email.get)
      UserContext.setUser(userInfo)
      block(request)
    } else {
      Future.successful {
        println("Destroying session")
        Results.Forbidden.discardingCookies(DiscardingCookie("pac4jCsrfToken"), DiscardingCookie("PLAY_SESSION"))
      }
    }
  }
}

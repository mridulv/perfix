package io.perfix.auth

import com.google.inject.Inject
import org.pac4j.core.context.session.SessionStore
import org.pac4j.core.profile.ProfileManager
import org.pac4j.play.PlayWebContext
import play.api.Logging
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters._

class AuthenticationAction @Inject() (parser: BodyParsers.Default,
                                      sessionStore: SessionStore)(implicit ec: ExecutionContext)
  extends ActionBuilderImpl(parser)
    with Logging {
  override def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) = {
    val webContext = new PlayWebContext(request)
    val profileManager = new ProfileManager(webContext, sessionStore)
    val profiles = profileManager.getProfiles.asScala

    if (profiles.nonEmpty) {
      block(request)
    } else {
      Future.successful {
        Results.Forbidden
      }
    }
  }
}

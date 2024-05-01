package io.perfix.auth
import org.pac4j.core.context.session.SessionStore
import org.pac4j.core.context.WebContext
import org.pac4j.core.profile.CommonProfile
import play.cache.AsyncCacheApi
import play.core.j.PlayMagicForJava.javaOptionToScala

import scala.compat.java8.FutureConverters._
import scala.concurrent.{ExecutionContext, Future}

class PlayCacheSessionStore(cache: AsyncCacheApi)(implicit ec: ExecutionContext) extends SessionStore {

  override def get(context: WebContext, key: String): Future[Object] = {
    cache.get[Object](key).toScala.map(_.orNull)
  }

  override def set(context: WebContext, key: String, value: Object): Future[Unit] = {
    cache.set(key, value).toScala.map(_ => ())
  }

  override def destroySession(context: WebContext): Future[Boolean] = {
    val sessionId = getSessionId(context, false)
    if (sessionId != null) {
      cache.remove(sessionId).toScala.map(_ => true)
    } else {
      Future.successful(false)
    }
  }

  override def getTrackableSession(context: WebContext): Future[Object] = {
    Future.successful(getSessionId(context, false))
  }

  override def buildFromTrackableSession(context: WebContext, trackableSession: Object): Future[SessionStore] = {
    Future.successful(this)
  }

  override def renewSession(context: WebContext): Future[Boolean] = Future.successful(true)

  private def getSessionId(context: WebContext, create: Boolean): String = {
    // Implement logic to retrieve or create a session ID
    "sessionId"
  }
}

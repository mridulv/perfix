package io.perfix.auth

import com.google.inject.{AbstractModule, Inject, Provider, Provides}
import org.pac4j.core.authorization.authorizer.RequireAnyRoleAuthorizer
import org.pac4j.core.client.Clients
import org.pac4j.core.config.Config
import org.pac4j.core.context.{FrameworkParameters, WebContext}
import org.pac4j.core.context.session.{SessionStore, SessionStoreFactory}
import org.pac4j.core.matching.matcher.PathMatcher
import org.pac4j.core.profile.CommonProfile
import org.pac4j.oauth.client.Google2Client
import org.pac4j.play.scala.{DefaultSecurityComponents, Pac4jScalaTemplateHelper, SecurityComponents}
import org.pac4j.play.store.{PlayCookieSessionStore, ShiroAesDataEncrypter}
import org.pac4j.play.{CallbackController, LogoutController}
import play.api.{Configuration, Environment}

import java.nio.charset.StandardCharsets

class SecurityModule(environment: Environment, configuration: Configuration) extends AbstractModule {
  val baseUrl = configuration.get[String]("baseUrl")

  override def configure(): Unit = {
    val sKey = configuration.get[String]("play.http.secret.key").substring(0, 16)
    val dataEncrypter = new ShiroAesDataEncrypter(sKey.getBytes(StandardCharsets.UTF_8))
    val playSessionStore = new PlayCookieSessionStore(dataEncrypter)
    bind(classOf[SessionStore]).toInstance(playSessionStore)

    bind(classOf[SecurityComponents]).to(classOf[DefaultSecurityComponents])

    bind(classOf[Pac4jScalaTemplateHelper[CommonProfile]])

    // callback
    val callbackController = new CallbackController()
    callbackController.setDefaultUrl("/?defaulturlafterlogout")
    bind(classOf[CallbackController]).toInstance(callbackController)

    // logout
    val logoutController = new LogoutController()
    logoutController.setDefaultUrl("/")
    bind(classOf[LogoutController]).toInstance(logoutController)
  }

  @Provides
  def provideGoogleClient: Google2Client = {
    val google2Id = configuration.getOptional[String]("googleId").get
    val google2Secret = configuration.getOptional[String]("googleSecret").get
    val google2Client = new Google2Client(google2Id, google2Secret)
    google2Client.setMultiProfile(true)
    google2Client
  }

  @Provides
  def provideConfig(google2Client: Google2Client, sessionStore: SessionStore): Config = {
    val clients = new Clients(baseUrl + "/callback", google2Client)

    val config = new Config(clients)
    config.addAuthorizer("admin", new RequireAnyRoleAuthorizer("ROLE_ADMIN"))
    config.addAuthorizer("custom", new CustomAuthorizer)
    config.addMatcher("excludedPath", new PathMatcher().excludeRegex("^/facebook/notprotected\\.html$"))
    config.setSessionStoreFactory(new SessionStoreFactory {
      override def newSessionStore(parameters: FrameworkParameters): SessionStore = sessionStore
    });
    config.setHttpActionAdapter(new DemoHttpActionAdapter())
    config
  }
}

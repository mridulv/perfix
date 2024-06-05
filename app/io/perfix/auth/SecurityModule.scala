package io.perfix.auth

import com.google.inject.{AbstractModule, Provides, Singleton}
import org.pac4j.core.authorization.authorizer.RequireAnyRoleAuthorizer
import org.pac4j.core.client.Clients
import org.pac4j.core.config.Config
import org.pac4j.core.context.session.SessionStore
import org.pac4j.oauth.client.Google2Client
import org.pac4j.oauth.client.Google2Client.Google2Scope
import org.pac4j.play.scala.{DefaultSecurityComponents, SecurityComponents}
import org.pac4j.play.store.{NoOpDataEncrypter, PlayCookieSessionStore}
import org.pac4j.play.{CallbackController, LogoutController}
import play.api.{Configuration, Environment}

class SecurityModule(environment: Environment, configuration: Configuration) extends AbstractModule {
  private val BASE_URL: String = configuration.get[String]("pac4j.googleClient.baseUrl")
  private val CLIENT_ID: String = configuration.get[String]("pac4j.googleClient.clientId")
  private val SECRET: String = configuration.get[String]("pac4j.googleClient.secret")


  override def configure(): Unit = {
    val dataEncrypter = new NoOpDataEncrypter
    val playSessionStore = new PlayCookieSessionStore(dataEncrypter)
    bind(classOf[SessionStore]).toInstance(playSessionStore)

    bind(classOf[SecurityComponents]).to(classOf[DefaultSecurityComponents])

    val callbackController = new CallbackController()
    callbackController.setDefaultUrl(BASE_URL + "/me")
    bind(classOf[CallbackController]).toInstance(callbackController)

    // logout
    val logoutController = new LogoutController()
    logoutController.setDefaultUrl(BASE_URL + "/me")
    bind(classOf[LogoutController]).toInstance(logoutController)
  }

  @Provides @Singleton
  def provideGoogleClient: Google2Client = {
    val google2Client = new Google2Client(CLIENT_ID, SECRET)
    google2Client.setMultiProfile(true)
    google2Client.setScope(Google2Scope.EMAIL_AND_PROFILE)
    google2Client
  }

  @Provides @Singleton
  def provideConfig(google2Client: Google2Client, sessionStore: SessionStore): Config = {
    val clients = new Clients(BASE_URL + "/callback", google2Client)

    val config = new Config(clients)
    config.addAuthorizer("admin", new RequireAnyRoleAuthorizer("ROLE_ADMIN"))
    config.addAuthorizer("custom", new CustomAuthorizer)
    config.setHttpActionAdapter(new DemoHttpActionAdapter())
    config
  }
}
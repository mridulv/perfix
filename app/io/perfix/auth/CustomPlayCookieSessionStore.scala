package io.perfix.auth

import io.perfix.auth.CustomPlayCookieSessionStore._
import org.pac4j.core.context.WebContext
import org.pac4j.core.util.Pac4jConstants
import org.pac4j.play.store.{DataEncrypter, PlayCookieSessionStore}

import java.util

class CustomPlayCookieSessionStore(dataEncrypter: DataEncrypter) extends PlayCookieSessionStore(dataEncrypter) {

  override def set(context: WebContext, key: String, value: AnyRef): Unit = {
    val values: util.Map[String, AnyRef] = getSessionValues(context)
    if (value == null) {
      values.remove(key)
    }
    else {
      var clearedValue: AnyRef = value
      if (Pac4jConstants.USER_PROFILES == key) clearedValue = clearUserProfiles(value)
      values.put(key, clearedValue)
    }
    val timeout: Long = System.currentTimeMillis() + PAC4J_SESSION_TIMEOUT_VALUE_IN_SECS * 1000L
    values.put(PAC4J_SESSION_TIMEOUT, timeout.asInstanceOf[AnyRef])
    putSessionValues(context, values)
  }

}

object CustomPlayCookieSessionStore {

  val PAC4J_SESSION_TIMEOUT = "pac4jSessionTimeout"
  val PAC4J_SESSION_TIMEOUT_VALUE_IN_SECS: Long = 60L

}

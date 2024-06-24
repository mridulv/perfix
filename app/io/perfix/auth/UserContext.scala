package io.perfix.auth

import io.perfix.model.UserInfo

object UserContext {
  private val userThreadLocal = new ThreadLocal[UserInfo]()

  def setUser(user: UserInfo): Unit = userThreadLocal.set(user)

  def getUser: Option[UserInfo] = Option(userThreadLocal.get())

  def clear(): Unit = userThreadLocal.remove()
}

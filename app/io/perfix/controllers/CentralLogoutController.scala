package io.perfix.controllers

import io.perfix.model.{DatasetId, DatasetParams}
import org.pac4j.core.profile.CommonProfile
import org.pac4j.play.LogoutController
import org.pac4j.play.scala.Security
import play.api.libs.json.Json
import play.api.mvc.{BaseController, Results}
import play.mvc.Controller

class CentralLogoutController extends LogoutController {
  setDefaultUrl("http://localhost:9000/?defaulturlafterlogoutafteridp")
  setLocalLogout(false)
  setCentralLogout(true)
  setLogoutUrlPattern("http://localhost:9000/.*")
}

class A  extends Security[CommonProfile] {

  def all = Secure("Google2Client") { profiles =>
    Action(parse.json) { request =>
      val datasetParams = request.body.as[DatasetParams]
      Results.Ok
    }
  }

}
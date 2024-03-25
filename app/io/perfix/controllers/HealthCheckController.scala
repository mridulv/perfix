package io.perfix.controllers

import com.google.inject.{Inject, Singleton}
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}

@Singleton
class HealthCheckController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {
  def health: Action[AnyContent] = Action {
    Ok("Healthy")
  }
}

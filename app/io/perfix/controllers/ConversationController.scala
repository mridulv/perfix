package io.perfix.controllers

import io.perfix.auth.AuthenticationAction

import javax.inject.{Inject, Singleton}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import io.perfix.manager.ConversationManager
import io.perfix.model.api.{ConversationId, ConversationParams}
import org.pac4j.core.profile.UserProfile
import org.pac4j.play.scala.{Security, SecurityComponents}

import scala.concurrent.ExecutionContext

@Singleton
class ConversationController @Inject()(val controllerComponents: SecurityComponents,
                                       authenticationAction: AuthenticationAction,
                                       conversationManager: ConversationManager)(implicit ec: ExecutionContext)
  extends Security[UserProfile] {

  def create: Action[JsValue] = authenticationAction(parse.json) { request =>
    val conversationParams = request.body.as[ConversationParams]
    val createdConversation = conversationManager.create(conversationParams)
    Ok(Json.toJson(createdConversation))
  }

  def get(id: Int): Action[AnyContent] = authenticationAction {
    val conversation = conversationManager.get(ConversationId(id))
    conversation match {
      case Some(params) => Ok(Json.toJson(params))
      case None => NotFound
    }
  }

  def list: Action[AnyContent] = authenticationAction {
    val conversations = conversationManager.list()
    Ok(Json.toJson(conversations))
  }

  def delete(id: Int): Action[AnyContent] = authenticationAction {
    val deletedRows = conversationManager.delete(ConversationId(id))
    if (deletedRows > 0) Ok else NotFound
  }
}

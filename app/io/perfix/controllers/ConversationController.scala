package io.perfix.controllers

import io.perfix.auth.AuthenticationAction

import javax.inject.{Inject, Singleton}
import play.api.libs.json.{JsString, JsValue, Json}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import io.perfix.manager.ConversationManager
import io.perfix.model.api.{ConversationId, ConversationMessage, ConversationParams}
import org.pac4j.core.profile.UserProfile
import org.pac4j.play.scala.{Security, SecurityComponents}

import scala.concurrent.ExecutionContext
import scala.util.parsing.json.JSONObject

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
    try {
      Ok(Json.toJson(conversationManager.get(ConversationId(id))))
    } catch {
      case e: Exception => NotFound
    }
  }

  def converse(id: Int): Action[JsValue] = authenticationAction(parse.json) { request =>
    val message = (request.body \ "message").as[String]
    val conversationResponse = conversationManager.converse(ConversationId(id), message)
    Ok(Json.obj("message" -> conversationResponse))
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

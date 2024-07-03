package io.perfix.controllers

import javax.inject.{Inject, Singleton}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import io.perfix.manager.ConversationManager
import io.perfix.model.api.{ConversationId, ConversationParams}
import scala.concurrent.ExecutionContext

@Singleton
class ConversationController @Inject()(cc: ControllerComponents,
                                       conversationManager: ConversationManager)
                                      (implicit ec: ExecutionContext) extends AbstractController(cc) {

  def create: Action[JsValue] = Action(parse.json) { request =>
    val conversationParams = request.body.as[ConversationParams]
    val createdConversation = conversationManager.create(conversationParams)
    Ok(Json.toJson(createdConversation))
  }

  def get(id: Int): Action[AnyContent] = Action {
    val conversation = conversationManager.get(ConversationId(id))
    conversation match {
      case Some(params) => Ok(Json.toJson(params))
      case None => NotFound
    }
  }

  def list: Action[AnyContent] = Action {
    val conversations = conversationManager.list()
    Ok(Json.toJson(conversations))
  }

  def delete(id: Int): Action[AnyContent] = Action {
    val deletedRows = conversationManager.delete(ConversationId(id))
    if (deletedRows > 0) Ok else NotFound
  }
}

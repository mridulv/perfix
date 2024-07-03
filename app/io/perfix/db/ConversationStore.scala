package io.perfix.db

import com.google.inject.{Inject, Singleton}
import io.perfix.auth.UserContext
import io.perfix.db.tables.ConversationTable
import io.perfix.exceptions.UserNotDefinedException
import io.perfix.model.UserInfo
import io.perfix.model.api.{ConversationId, ConversationParams}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.lifted.TableQuery

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

@Singleton
class ConversationStore @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  private val conversationTable = TableQuery[ConversationTable]
  import dbConfig._
  import profile.api._

  def create(conversationParams: ConversationParams): ConversationParams = unwrapFuture { userInfo =>
    db.run {
      val conversationRow = conversationParams
        .copy(createdAt = Some(System.currentTimeMillis()))
        .toConversationRow(userInfo.email)
      (conversationTable returning conversationTable.map(_.id)
        into ((conversationRow, id) => conversationRow.copy(id = id))
        ) += conversationRow
    }
  }.toConversationRow

  def update(conversationId: ConversationId, conversationParams: ConversationParams): Int = unwrapFuture { userInfo =>
    db.run {
      val conversationRow = conversationParams.toConversationRow(userInfo.email)
      val query = for {
        e <- conversationTable
        if e.id === conversationId.id && e.userEmail === userInfo.email
      } yield e.obj
      query.update(conversationRow.obj)
    }
  }

  def get(conversationId: ConversationId): Option[ConversationParams] = unwrapFuture { userInfo =>
    db.run {
      conversationTable
        .filter(_.id === conversationId.id)
        .filter(_.userEmail === userInfo.email)
        .result
        .headOption
        .map(_.map(_.toConversationRow))
    }
  }


  def list(): Seq[ConversationParams] = unwrapFuture { userInfo =>
    db.run {
      conversationTable
        .filter(_.userEmail === userInfo.email)
        .result
        .map(_.map(_.toConversationRow))
    }
  }

  def delete(conversationId: ConversationId): Int = unwrapFuture { userInfo =>
    db.run {
      conversationTable
        .filter(_.userEmail === userInfo.email)
        .filter(_.id === conversationId.id)
        .delete
    }
  }

  private def unwrapFuture[A](f: UserInfo => Future[A]): A = {
    val userInfo = UserContext.getUser
    userInfo match {
      case Some(user) => Await.result(f(user), Duration.Inf)
      case None => throw UserNotDefinedException()
    }
  }
}

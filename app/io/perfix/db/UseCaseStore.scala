package io.perfix.db

import com.google.inject.{Inject, Singleton}
import io.perfix.auth.UserContext
import io.perfix.db.tables.UseCaseTable
import io.perfix.exceptions.UserNotDefinedException
import io.perfix.model.UserInfo
import io.perfix.model.api.{UseCaseId, UseCaseParams}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.lifted.TableQuery

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

@Singleton
class UseCaseStore @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  private val useCaseTable = TableQuery[UseCaseTable]
  import dbConfig._
  import profile.api._

  def create(useCaseParams: UseCaseParams): UseCaseParams = unwrapFuture { userInfo =>
    db.run {
      val useCaseRow = useCaseParams
        .copy(createdAt = Some(System.currentTimeMillis()))
        .toUseCaseRow(userInfo.email)
      (useCaseTable returning useCaseTable.map(_.id)
        into ((useCaseRow, id) => useCaseRow.copy(id = id))
        ) += useCaseRow
    }
  }.toUseCaseRow

  def update(useCaseId: UseCaseId, useCaseParams: UseCaseParams): Int = unwrapFuture { userInfo =>
    db.run {
      val useCaseRow = useCaseParams.toUseCaseRow(userInfo.email)
      val query = for {
        e <- useCaseTable
        if e.id === useCaseId.id && e.userEmail === userInfo.email
      } yield e.obj
      query.update(useCaseRow.obj)
    }
  }

  def get(useCaseId: UseCaseId): Option[UseCaseParams] = unwrapFuture { userInfo =>
    db.run {
      useCaseTable
        .filter(_.id === useCaseId.id)
        .filter(_.userEmail === userInfo.email)
        .result
        .headOption
        .map(_.map(_.toUseCaseRow))
    }
  }


  def list(): Seq[UseCaseParams] = unwrapFuture { userInfo =>
    db.run {
      useCaseTable
        .filter(_.userEmail === userInfo.email)
        .result
        .map(_.map(_.toUseCaseRow))
    }
  }

  def delete(useCaseId: UseCaseId): Int = unwrapFuture { userInfo =>
    db.run {
      useCaseTable
        .filter(_.userEmail === userInfo.email)
        .filter(_.id === useCaseId.id)
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

package io.perfix.store

import com.google.inject.{Inject, Singleton}
import io.perfix.auth.UserContext
import io.perfix.exceptions.UserNotDefinedException
import io.perfix.model.api.{DatabaseConfigId, DatabaseConfigParams}
import io.perfix.model.UserInfo
import io.perfix.store.tables.DatabaseConfigTable
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.lifted.TableQuery

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

@Singleton
class DatabaseConfigStore @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  private val databaseConfigTable = TableQuery[DatabaseConfigTable]
  import dbConfig._
  import profile.api._

  def create(databaseConfigParams: DatabaseConfigParams): DatabaseConfigParams = unwrapFuture { userInfo =>
    db.run {
      val databaseConfigRow = databaseConfigParams
        .copy(createdAt = Some(System.currentTimeMillis()))
        .toDatabaseConfigRow(userInfo.email)
      (databaseConfigTable returning databaseConfigTable.map(_.id)
        into ((databaseConfigRow, id) => databaseConfigRow.copy(id=id))
        ) += databaseConfigRow
    }
  }.toDatabaseConfigParams

  def update(databaseConfigId: DatabaseConfigId,
             databaseConfigParams: DatabaseConfigParams): Int = unwrapFuture { userInfo =>
    db.run {
      val databaseConfigRow = databaseConfigParams.toDatabaseConfigRow(userInfo.email)
      val query = for {
        e <- databaseConfigTable
        if e.id === databaseConfigId.id && e.userEmail === userInfo.email
      } yield e.obj
      query.update(databaseConfigRow.obj)
    }
  }

  def get(databaseConfigId: DatabaseConfigId): Option[DatabaseConfigParams] = unwrapFuture { userInfo =>
    db.run {
      databaseConfigTable
        .filter(_.id === databaseConfigId.id)
        .filter(_.userEmail === userInfo.email)
        .result
        .headOption
        .map(_.map(_.toDatabaseConfigParams))
    }
  }

  def list(): Seq[DatabaseConfigParams] = unwrapFuture { userInfo =>
    db.run {
      databaseConfigTable
        .filter(_.userEmail === userInfo.email)
        .result
        .map(_.map(_.toDatabaseConfigParams))
    }
  }

  def delete(databaseConfigId: DatabaseConfigId): Int = unwrapFuture { userInfo =>
    db.run {
      databaseConfigTable
        .filter(_.userEmail === userInfo.email)
        .filter(_.id === databaseConfigId.id)
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
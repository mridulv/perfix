package io.perfix.stores

import io.perfix.launch.StoreLauncher
import io.perfix.model.store.DatabaseConfigParams
import io.perfix.query.PerfixQuery

trait DataStore[T <: DatabaseConfigParams] {

  val databaseConfigParams: T
  def launcher(): Option[StoreLauncher[T]]
  def connectAndInitialize(): Unit
  def putData(rows: Seq[Map[String, Any]]): Unit
  def readData(perfixQuery: PerfixQuery): Seq[Map[String, Any]]
  def cleanup(): Unit
}


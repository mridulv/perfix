package io.perfix.stores

import io.perfix.launch.StoreLauncher
import io.perfix.model.store.StoreParams
import io.perfix.query.PerfixQuery

trait DataStore[T <: StoreParams] {

  val storeParams: T
  def launcher(): Option[StoreLauncher[T]]
  def connectAndInitialize(): Unit
  def putData(rows: Seq[Map[String, Any]]): Unit
  def readData(perfixQuery: PerfixQuery): Seq[Map[String, Any]]
  def cleanup(): Unit
}


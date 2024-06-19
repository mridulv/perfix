package io.perfix.stores

import io.perfix.launch.StoreLauncher
import io.perfix.model.store.DatabaseSetupParams
import io.perfix.query.PerfixQuery

trait DataStore {

  val databaseConfigParams: DatabaseSetupParams
  def connectAndInitialize(): Unit
  def putData(rows: Seq[Map[String, Any]]): Unit
  def readData(perfixQuery: PerfixQuery): Seq[Map[String, Any]]
  def cleanup(): Unit
}


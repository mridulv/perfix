package io.perfix.stores

import io.perfix.model.DataDescription
import io.perfix.query.PerfixQuery
import io.perfix.stores.mysql.MySQLQuestionnaire

trait DataStore {
  def storeInputs(dataDescription: DataDescription): MySQLQuestionnaire
  def connectAndInitialize(): Unit
  def putData(): Unit
  def convertQuery(perfixQuery: PerfixQuery): String
  def readData(query: String): Seq[Map[String, Any]]
  def cleanup(): Unit
}

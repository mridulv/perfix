package io.perfix.stores

import io.perfix.query.PerfixQuery
import io.perfix.question.Questionnaire

trait DataStore {
  def questions(): Questionnaire
  def initialize(): Unit
  def putData(): Unit
  def convertQuery(perfixQuery: PerfixQuery): String
  def readData(query: String): Seq[Map[String, Any]]
}

case class InitStoreParams()

case class StoreData()
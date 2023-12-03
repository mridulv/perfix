package io.perfix.stores

import io.perfix.stores.question.Questionnaire

trait DataStore {
  def questions(): Questionnaire
  def initialize(): Unit
  def putData(data: Seq[Map[String, Any]], rate: Int): Unit
  def readData(query: String): Seq[Map[String, Any]]
}

case class InitStoreParams()

case class StoreData()
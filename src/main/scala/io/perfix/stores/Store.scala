package io.perfix.stores

import io.perfix.stores.question.{Questionnaire, StoreQuestionParams}

trait DataStore {
  def questions(): Questionnaire
  def initialize(): Unit
  def putData(rate: Int): Unit
  def readData(query: String): Seq[Map[String, Any]]
}

case class InitStoreParams()

case class StoreData()
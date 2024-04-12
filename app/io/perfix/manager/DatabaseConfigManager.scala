package io.perfix.manager

import com.google.inject.Singleton
import io.perfix.model.{DatabaseConfigId, DatabaseConfigParams}

@Singleton
class DatabaseConfigManager {

  def create(databaseConfigParams: DatabaseConfigParams): DatabaseConfigId = ???

  def get(databaseConfigId: DatabaseConfigId): DatabaseConfigParams = ???

  def all(): Seq[DatabaseConfigParams] = ???

}

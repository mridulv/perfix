package io.perfix.manager

import com.google.inject.Singleton
import io.perfix.model.{DatabaseConfigId, DatabaseConfigParams, FormInputs, FormInputValues}

import scala.collection.mutable
import scala.util.Random

@Singleton
class DatabaseConfigManager {
  private val mapping: mutable.Map[DatabaseConfigId, DatabaseConfigParams] = mutable.Map.empty[DatabaseConfigId, DatabaseConfigParams]

  def create(databaseConfigParams: DatabaseConfigParams): DatabaseConfigId = {
    val id: Int = Random.nextInt()
    mapping.put(
      DatabaseConfigId(id),
      databaseConfigParams.copy(databaseConfigId = Some(DatabaseConfigId(id)))
    )
    DatabaseConfigId(id)
  }

  def get(databaseConfigId: DatabaseConfigId): DatabaseConfigParams = {
    mapping(databaseConfigId)
  }

  def getInputs(databaseConfigId: DatabaseConfigId): FormInputs = {
    mapping(databaseConfigId).formInputs
  }

  def submitInputs(databaseConfigId: DatabaseConfigId,
                   formInputValues: FormInputValues): DatabaseConfigParams = {
    val databaseConfigParams = mapping(databaseConfigId)
    databaseConfigParams.copy(formInputValues = Some(formInputValues.values))
  }

  def update(databaseConfigId: DatabaseConfigId,
             databaseConfigParams: DatabaseConfigParams): DatabaseConfigParams = {
    mapping.put(
      databaseConfigId,
      databaseConfigParams.copy(databaseConfigId = Some(databaseConfigId))
    )
    databaseConfigParams
  }

  def all(): Seq[DatabaseConfigParams] = {
    mapping.values.toSeq
  }

}

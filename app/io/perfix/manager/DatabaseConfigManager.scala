package io.perfix.manager

import com.google.inject.Singleton
import io.perfix.model.{DatabaseConfigId, DatabaseConfigParams, FormDetails, FormInputValues, FormInputs}

import scala.collection.mutable
import scala.util.Random

@Singleton
class DatabaseConfigManager {
  private val mapping: mutable.Map[DatabaseConfigId, DatabaseConfigParams] = mutable.Map.empty[DatabaseConfigId, DatabaseConfigParams]
  private val formManagerMapping: mutable.Map[DatabaseConfigId, FormManager] = mutable.Map.empty[DatabaseConfigId, FormManager]

  def create(databaseConfigParams: DatabaseConfigParams): DatabaseConfigId = {
    val id: Int = Random.nextInt()
    val updatedDatabaseConfigParams = databaseConfigParams.copy(databaseConfigId = Some(DatabaseConfigId(id)))
    val databaseConfigId = DatabaseConfigId(id)
    mapping.put(
      databaseConfigId,
      updatedDatabaseConfigParams
    )
    formManagerMapping.put(databaseConfigId, new FormManager(databaseConfigParams))
    println(s"Adding form with id: $databaseConfigId to the mapping")
    databaseConfigId
  }

  def currentForm(databaseConfigId: DatabaseConfigId): Option[FormInputs] = {
    println(s"Getting current form for $databaseConfigId")
    formManagerMapping(databaseConfigId).current
  }

  def submitForm(databaseConfigId: DatabaseConfigId,
                 formInputValues: FormInputValues): Unit = {
    val databaseConfigParams = mapping(databaseConfigId)
    println(s"Moving to the next form : $databaseConfigId")
    val formManager = formManagerMapping(databaseConfigId)
    val inputs = formManager.submit
    val updatedFormDetails = databaseConfigParams.formDetails.getOrElse(FormDetails.empty).addValues(formInputValues)
    val updatedConfig = if (formManager.current.isEmpty) {
      databaseConfigParams.copy(formDetails = Some(updatedFormDetails.complete))
    } else {
      databaseConfigParams.copy(formDetails = Some(updatedFormDetails))
    }
    mapping.put(databaseConfigId, updatedConfig)
  }

  def get(databaseConfigId: DatabaseConfigId): DatabaseConfigParams = {
    mapping(databaseConfigId)
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

  def delete(databaseConfigId: DatabaseConfigId): Unit = {
    mapping.remove(databaseConfigId)
  }

}
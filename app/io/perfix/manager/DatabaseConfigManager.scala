package io.perfix.manager

import com.google.inject.Singleton
import io.perfix.model._

import scala.collection.mutable
import scala.util.Random

@Singleton
class DatabaseConfigManager {
  private val mapping: mutable.Map[DatabaseConfigId, DatabaseConfigParams] = mutable.Map.empty[DatabaseConfigId, DatabaseConfigParams]
  private val formManagerMapping: mutable.Map[DatabaseConfigId, FormManager] = mutable.Map.empty[DatabaseConfigId, FormManager]

  def create(databaseConfigParams: DatabaseConfigParams): DatabaseConfigId = {
    val id: Int = Random.nextInt()
    val updatedDatabaseConfigParams = databaseConfigParams
      .copy(databaseConfigId = Some(DatabaseConfigId(id)))
      .copy(formDetails = databaseConfigParams.formDetails.map(_.copy(formStatus = InComplete)))
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
                 formInputValues: FormInputValues): Option[FormStatus] = {
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
    updatedConfig.formDetails.map(_.formStatus)
  }

  def get(databaseConfigId: DatabaseConfigId): DatabaseConfigParams = {
    mapping(databaseConfigId)
  }

  def update(databaseConfigId: DatabaseConfigId,
             databaseConfigParams: DatabaseConfigParams): DatabaseConfigParams = {
    val exitingParams = mapping(databaseConfigId)
    val updatedParams = exitingParams
      .copy(name = databaseConfigParams.name)
      .copy(formDetails = exitingParams.formDetails.map(_.copy(formStatus = Updating)))
    mapping.put(
      databaseConfigId,
      updatedParams
    )
    formManagerMapping.put(databaseConfigId, new FormManager(updatedParams))
    updatedParams
  }

  def all(): Seq[DatabaseConfigParams] = {
    mapping.values.toSeq
  }

  def delete(databaseConfigId: DatabaseConfigId): Unit = {
    mapping.remove(databaseConfigId)
  }

}

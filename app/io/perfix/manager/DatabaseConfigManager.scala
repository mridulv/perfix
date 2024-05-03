package io.perfix.manager

import com.google.inject.{Inject, Singleton}
import io.perfix.exceptions.InvalidStateException
import io.perfix.model._
import io.perfix.store.DatabaseConfigStore

import scala.collection.mutable

@Singleton
class DatabaseConfigManager @Inject()(databaseConfigStore: DatabaseConfigStore) {
  private val formManagerMapping: mutable.Map[DatabaseConfigId, FormManager] = mutable.Map.empty[DatabaseConfigId, FormManager]

  def create(databaseConfigParams: DatabaseConfigParams): DatabaseConfigId = {
    val databaseConfigId = databaseConfigStore.create(databaseConfigParams)
      .databaseConfigId
      .getOrElse(throw InvalidStateException("Invalid DatabaseConfig"))

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
    val databaseConfigParams = databaseConfigStore
      .get(databaseConfigId)
      .getOrElse(throw InvalidStateException("Invalid DatabaseConfigId"))
    println(s"Moving to the next form : $databaseConfigId")
    val formManager = formManagerMapping(databaseConfigId)
    val inputs = formManager.submit
    val updatedFormDetails = databaseConfigParams.formDetails.getOrElse(FormDetails.empty).addValues(formInputValues)
    val updatedConfig = if (formManager.current.isEmpty) {
      databaseConfigParams.copy(formDetails = Some(updatedFormDetails.complete))
    } else {
      databaseConfigParams.copy(formDetails = Some(updatedFormDetails))
    }
    databaseConfigStore.update(databaseConfigId, updatedConfig)
    updatedConfig.formDetails.map(_.formStatus)
  }

  def get(databaseConfigId: DatabaseConfigId): DatabaseConfigParams = {
    databaseConfigStore
      .get(databaseConfigId)
      .getOrElse(throw InvalidStateException("Invalid DatabaseConfigId"))
  }

  def update(databaseConfigId: DatabaseConfigId,
             databaseConfigParams: DatabaseConfigParams): DatabaseConfigParams = {
    val exitingParams = databaseConfigStore
      .get(databaseConfigId)
      .getOrElse(throw InvalidStateException("Invalid DatabaseConfigId"))
    val updatedParams = exitingParams
      .copy(name = databaseConfigParams.name)
      .copy(formDetails = exitingParams.formDetails.map(_.copy(formStatus = Updating)))
    databaseConfigStore.update(databaseConfigId, updatedParams)
    formManagerMapping.put(databaseConfigId, new FormManager(updatedParams))
    updatedParams
  }

  def all(): Seq[DatabaseConfigParams] = {
    databaseConfigStore.list()
  }

  def delete(databaseConfigId: DatabaseConfigId): Unit = {
    databaseConfigStore.delete(databaseConfigId)
  }

}

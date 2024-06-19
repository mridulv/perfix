package io.perfix.model

import io.perfix.model.DatabaseCategory.DatabaseCategory
import io.perfix.model.api.{DatabaseConfigParams, DatabaseFormInput}
import io.perfix.model.store.StoreType.StoreType
import play.api.libs.json.{Format, Json}

case class Database(name: StoreType,
                    databaseCategory: DatabaseCategory,
                    databaseFormInput: DatabaseFormInput,
                    databaseConfigParams: DatabaseConfigParams)

object Database {
  implicit val DatabaseFormatter: Format[Database] = Json.format[Database]
}

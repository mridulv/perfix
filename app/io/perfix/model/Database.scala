package io.perfix.model

import io.perfix.model.DatabaseCategory.DatabaseCategory
import io.perfix.model.api.DatabaseFormInput
import io.perfix.model.store.StoreType.StoreType

case class Database(name: StoreType,
                    databaseCategory: DatabaseCategory,
                    databaseFormInput: DatabaseFormInput, database)

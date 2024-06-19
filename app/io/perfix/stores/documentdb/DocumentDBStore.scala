package io.perfix.stores.documentdb

import com.mongodb.client.model.Indexes
import com.mongodb.client.{MongoClient, MongoClients, MongoCollection, MongoDatabase}
import io.perfix.exceptions.InvalidStateException
import io.perfix.launch.StoreLauncher
import io.perfix.model.api.DatasetParams
import io.perfix.query.PerfixQuery
import io.perfix.stores.DataStore
import org.bson.Document

import scala.jdk.CollectionConverters._

class DocumentDBStore(override val databaseConfigParams: DocumentDBDatabaseConfigParams)
  extends DataStore[DocumentDBDatabaseConfigParams] {

  private var mongoClient: MongoClient = _
  private var mongoDatabase: MongoDatabase = _

  override def launcher(): Option[StoreLauncher[DocumentDBDatabaseConfigParams]] = {
    Some(new DocumentDBLauncher(databaseConfigParams))
  }

  def connectAndInitialize(): Unit = {
    val connectionParams = databaseConfigParams.documentDBConnectionParams.getOrElse(
      throw InvalidStateException("Connection parameters should have been defined.")
    )
    val tableParams = databaseConfigParams.documentDBTableParams.getOrElse(
      throw InvalidStateException("Table parameters should have been defined.")
    )

    mongoClient = MongoClients.create(connectionParams.url)
    mongoDatabase = mongoClient.getDatabase(connectionParams.database)
    mongoDatabase.createCollection(tableParams.collectionName)

    databaseConfigParams.documentDBIndicesParams match {
      case Some(indicesParams) => mongoDatabase
        .getCollection(tableParams.collectionName)
        .createIndex(Indexes.ascending(indicesParams.columns: _*))
      case None => ()
    }
  }

  override def putData(rows: Seq[Map[String, Any]]): Unit = {
    val tableParams = databaseConfigParams.documentDBTableParams.getOrElse(
      throw InvalidStateException("Table parameters should have been defined.")
    )

    val collection: MongoCollection[Document] = mongoDatabase.getCollection(tableParams.collectionName)

    val documents = rows.map { row =>
      val document = new Document()
      row.map { case (k, v) =>
        document.put(k, v)
      }
      document
    }
    collection.insertMany(documents.asJava)
  }

  override def readData(perfixQuery: PerfixQuery): Seq[Map[String, Any]] = {
    val tableParams = databaseConfigParams.documentDBTableParams.getOrElse(
      throw InvalidStateException("Table parameters should have been defined.")
    )

    import com.mongodb.client.model.{Filters, Projections}

    val filter = perfixQuery.filtersOpt match {
      case Some(filter) => Filters.and(filter.map(filter => Filters.eq(filter.field, filter.fieldValue)).asJavaCollection)
      case None => Filters.and()
    }

    val projection = perfixQuery.projectedFieldsOpt match {
      case Some(projections) => Projections.fields(Projections.include(projections: _*))
      case None => Filters.and()
    }

    val cursor = mongoDatabase.getCollection(tableParams.collectionName)
      .find(filter)
      .projection(projection)
      .iterator()
    cursor.asScala.toList.map { c =>
      c.asScala.toMap
    }
  }

  override def cleanup(): Unit = {
    val tableParams = databaseConfigParams.documentDBTableParams.getOrElse(
      throw InvalidStateException("Table parameters should have been defined.")
    )
    mongoDatabase.getCollection(tableParams.collectionName).drop()
    mongoDatabase.drop()
    mongoClient.close()
  }
}
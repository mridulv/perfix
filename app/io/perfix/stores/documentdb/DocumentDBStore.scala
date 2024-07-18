package io.perfix.stores.documentdb

import com.mongodb.client.model.Indexes
import com.mongodb.client.{MongoClient, MongoClients, MongoCollection, MongoDatabase}
import io.perfix.exceptions.{InvalidStateException, WrongQueryException}
import io.perfix.query.{DBQuery, NoSqlDBQuery, SqlDBQuery, SqlDBQueryBuilder}
import io.perfix.stores.DataStore
import org.bson.Document

import scala.jdk.CollectionConverters._

class DocumentDBStore(override val databaseConfigParams: DocumentDBDatabaseSetupParams)
  extends DataStore {

  private var mongoClient: MongoClient = _
  private var mongoDatabase: MongoDatabase = _

  def connectAndInitialize(): Unit = {
    val connectionParams = databaseConfigParams.dbDetails.getOrElse(
      throw InvalidStateException("Connection parameters should have been defined.")
    )

    mongoClient = MongoClients.create(connectionParams.url)
    mongoDatabase = mongoClient.getDatabase(connectionParams.database)
    mongoDatabase.createCollection(databaseConfigParams.collectionName)

    mongoDatabase
      .getCollection(databaseConfigParams.collectionName)
      .createIndex(Indexes.ascending(databaseConfigParams.indices: _*))
  }

  override def putData(rows: Seq[Map[String, Any]]): Unit = {
    val collection: MongoCollection[Document] = mongoDatabase.getCollection(databaseConfigParams.collectionName)

    val documents = rows.map { row =>
      val document = new Document()
      row.map { case (k, v) =>
        document.put(k, v)
      }
      document
    }
    collection.insertMany(documents.asJava)
  }

  override def readData(dbQuery: DBQuery): Seq[Map[String, Any]] = {
    val noSqlDBQuery = dbQuery match {
      case noSqlDBQuery: NoSqlDBQuery => noSqlDBQuery
      case _: SqlDBQuery => throw WrongQueryException("Sql query not supported")
      case _: SqlDBQueryBuilder => throw WrongQueryException("Sql query not supported")
    }

    val collection = mongoDatabase.getCollection(databaseConfigParams.collectionName)

    // Build the query from the filters
    val mongoQuery = new Document()
    noSqlDBQuery.filters.foreach { filter =>
      filter.fieldValue match {
        case value: String => mongoQuery.put(filter.field, value)
        case value: Int => mongoQuery.put(filter.field, value)
        case value: Long => mongoQuery.put(filter.field, value)
        case value: Double => mongoQuery.put(filter.field, value)
        case value: Boolean => mongoQuery.put(filter.field, value)
        case _ => mongoQuery.put(filter.field, filter.fieldValue.toString)
      }
    }

    // Execute the query and collect the results
    val cursor = collection.find(mongoQuery).iterator()

    val results = scala.collection.mutable.ListBuffer[Map[String, Any]]()
    while (cursor.hasNext) {
      val doc = cursor.next()
      val map = doc.keySet().asScala.map { key =>
        key -> doc.get(key)
      }.toMap
      results += map
    }

    results.toSeq
  }

  override def cleanup(): Unit = {
    mongoDatabase.getCollection(databaseConfigParams.collectionName).drop()
    mongoDatabase.drop()
    mongoClient.close()
  }
}
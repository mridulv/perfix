package io.perfix.stores.documentdb

import com.mongodb.MongoClient
import com.mongodb.client.{MongoCollection, MongoDatabase}
import io.perfix.exceptions.InvalidStateException
import io.perfix.model.DataDescription
import io.perfix.query.PerfixQuery
import io.perfix.stores.DataStore
import org.bson.Document

import scala.jdk.CollectionConverters._

class DocumentDBStore extends DataStore {
  private var mongoClient: MongoClient = _
  private var mongoDatabase: MongoDatabase = _
  private var documentDBParams: DocumentDBParams = _
  private var dataDescription: DataDescription = _

  override def storeInputs(dataDescription: DataDescription): DocumentDBQuestionnaire = {
    this.dataDescription = dataDescription
    this.documentDBParams = DocumentDBParams(dataDescription)
    DocumentDBQuestionnaire(this.documentDBParams)
  }

  def connectAndInitialize(): Unit = {
    val connectionParams = documentDBParams.documentDBConnectionParams.getOrElse(
      throw InvalidStateException("Connection parameters should have been defined.")
    )
    val tableParams = documentDBParams.documentDBTableParams.getOrElse(
      throw InvalidStateException("Table parameters should have been defined.")
    )

    mongoClient = new MongoClient(connectionParams.url)
    mongoDatabase = mongoClient.getDatabase(connectionParams.url)
    mongoDatabase.createCollection(tableParams.collectionName)
  }

  override def putData(): Unit = {
    val tableParams = documentDBParams.documentDBTableParams.getOrElse(
      throw InvalidStateException("Table parameters should have been defined.")
    )

    val collection: MongoCollection[Document] = mongoDatabase.getCollection(tableParams.collectionName)

    val data = dataDescription.data
    val documents = data.map { row =>
      val document = new Document()
      row.map { case (k, v) =>
        document.put(k, v)
      }
      document
    }
    collection.insertMany(documents.asJava)
  }

  override def readData(perfixQuery: PerfixQuery): Seq[Map[String, Any]] = {
    val tableParams = documentDBParams.documentDBTableParams.getOrElse(
      throw InvalidStateException("Table parameters should have been defined.")
    )

    import com.mongodb.client.model.Filters
    import com.mongodb.client.model.Projections

    val filter = perfixQuery.filtersOpt match {
      case Some(filter) => Filters.and(filter.map(filter => Filters.eq(filter.field, filter.fieldValue)).asJavaCollection)
      case None => Filters.and()
    }

    val projection = perfixQuery.projectedFieldsOpt match {
      case Some(projections) => Projections.fields(Projections.include(projections: _*))
      case None => Filters.and()
    }

    val cursor = mongoDatabase.getCollection(tableParams.collectionName).find(Filters.and(filter, projection)).iterator()
    cursor.asScala.toList.map { c =>
      c.asScala.toMap
    }
  }

  override def cleanup(): Unit = {
    mongoDatabase.drop()
    mongoClient.close()
  }
}
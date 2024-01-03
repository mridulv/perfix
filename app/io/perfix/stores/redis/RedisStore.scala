package io.perfix.stores.redis

import io.perfix.context.QuestionExecutionContext
import io.perfix.exceptions.{InvalidStateException, PerfixQueryException}
import io.perfix.model.DataDescription
import io.perfix.query.PerfixQuery
import io.perfix.stores.DataStore
import redis.clients.jedis.{Jedis, JedisPool}

class RedisStore(questionExecutionContext: QuestionExecutionContext) extends DataStore {
  private var jedis: Jedis = _
  private var dataDescription: DataDescription = _
  private var redisParams: RedisParams = _

  override def storeInputs(dataDescription: DataDescription): RedisQuestionnaire = {
    this.dataDescription = dataDescription
    redisParams = RedisParams(dataDescription)
    new RedisQuestionnaire(redisParams, questionExecutionContext)
  }

  def connectAndInitialize(): Unit = {
    (redisParams.url, redisParams.port) match {
      case (Some(url), Some(port)) =>
        val pool = new JedisPool(url, port)
        jedis = pool.getResource
      case (_, _) => throw InvalidStateException("URL / Port Must be defined")
    }
  }

  override def putData(): Unit = {
    val data = dataDescription.data
    val keyColumn = redisParams.keyColumn.getOrElse(throw InvalidStateException("Key Column Must be defined"))
    data.map { row =>
      val key = row(keyColumn).toString
      val value = row.map { case (k, v) =>
        s"$k:$v"
      }.mkString(",")
      jedis.set(key, value)
    }
  }

  override def readData(perfixQuery: PerfixQuery): Seq[Map[String, Any]] = {
    val matchedValues = perfixQuery.filtersOpt.flatMap(_.headOption) match {
      case Some(filter) => jedis.get(s"${filter.field}:${filter.fieldValue}").split(",").flatMap { e =>
        val k = e.split(":").head
        val v = e.split(":").reverse.head
        if (perfixQuery.projectedFieldsOpt.getOrElse(List.empty).contains(k)) {
          Some((k -> v))
        } else {
          None
        }
      }.toMap
      case None => throw PerfixQueryException("For RedisStore, filter should be present")
    }
    Seq(matchedValues)
  }

  override def cleanup(): Unit = {
    jedis.flushAll()
  }
}

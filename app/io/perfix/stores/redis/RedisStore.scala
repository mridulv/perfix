package io.perfix.stores.redis

import io.perfix.exceptions.{InvalidStateException, PerfixQueryException}
import io.perfix.model.DataDescription
import io.perfix.query.PerfixQuery
import io.perfix.stores.DataStore
import redis.clients.jedis.JedisPool

class RedisStore extends DataStore {
  private var jedisPool: JedisPool = _
  private var dataDescription: DataDescription = _
  private var redisParams: RedisParams = _

  override def storeInputs(dataDescription: DataDescription): RedisQuestionnaire = {
    this.dataDescription = dataDescription
    redisParams = RedisParams(dataDescription)
    new RedisQuestionnaire(redisParams)
  }

  def connectAndInitialize(): Unit = {
    (redisParams.url, redisParams.port) match {
      case (Some(url), Some(port)) =>
        jedisPool = new JedisPool(url, port)
        jedisPool.setMinIdle(100)
      case (_, _) => throw InvalidStateException("URL / Port Must be defined")
    }
  }

  override def putData(): Unit = {
    val data = dataDescription.data
    val jedis = jedisPool.getResource
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
    val jedis = jedisPool.getResource
    val matchedValues = perfixQuery.filtersOpt.flatMap(_.headOption) match {
      case Some(filter) =>
        Option(jedis.get(filter.fieldValue.toString)).flatMap { v =>
          val value = v.split(",").flatMap { e =>
            val k = e.split(":").head
            val v = e.split(":").reverse.head
            if (perfixQuery.projectedFieldsOpt.getOrElse(List.empty).contains(k)) {
              Some(k -> v)
            } else {
              None
            }
          }.toMap
          Some(value)
        }
      case None => throw PerfixQueryException("For RedisStore, filter should be present")
    }
    jedisPool.returnResource(jedis)
    Seq(matchedValues).flatten
  }

  override def cleanup(): Unit = {
    jedisPool.getResource.flushAll()
  }
}

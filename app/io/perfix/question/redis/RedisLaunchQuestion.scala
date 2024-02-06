package io.perfix.question.redis

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.regions.Regions
import com.amazonaws.services.elasticache.AmazonElastiCacheClientBuilder
import com.amazonaws.services.elasticache.model._
import io.perfix.launch.{AWSCloudCredentials, LaunchStoreQuestion}
import io.perfix.model.{IntType, QuestionType, StringType}
import io.perfix.question.Question.QuestionLabel
import io.perfix.question.redis.ElastiCacheLaunchQuestion._
import io.perfix.stores.redis.RedisParams

class RedisLaunchQuestion(override val credentials: AWSCloudCredentials,
                          override val storeQuestionParams: RedisParams) extends LaunchStoreQuestion {

  override val mapping: Map[QuestionLabel, QuestionType] = Map(
    CLUSTER_ID -> QuestionType(StringType),
    CACHE_NODE_TYPE -> QuestionType(StringType),
    NUM_CACHE_NODES -> QuestionType(IntType)
  )

  override def setAnswers(answers: Map[QuestionLabel, Any]): Unit = {
    val clusterId = answers(CLUSTER_ID).toString
    val cacheNodeType = answers(CACHE_NODE_TYPE).toString
    val numCacheNodes = answers(NUM_CACHE_NODES).toString.toInt

    val elasticacheClient = AmazonElastiCacheClientBuilder.standard()
      .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
      .withRegion(Regions.US_EAST_1)
      .build()

    val createCacheClusterRequest = new CreateCacheClusterRequest()
      .withCacheClusterId(clusterId)
      .withCacheNodeType(cacheNodeType)
      .withEngine("redis")
      .withNumCacheNodes(numCacheNodes)
      .withPreferredAvailabilityZone("us-east-1a")

    try {
      val response = elasticacheClient.createCacheCluster(createCacheClusterRequest)

      storeQuestionParams.url = Some(response.getConfigurationEndpoint.getAddress)
      storeQuestionParams.port = Some(response.getConfigurationEndpoint.getPort)

      println(s"Redis cluster creation initiated: ${response.getCacheClusterId}")
    } catch {
      case ex: Exception =>
        println(s"Error creating ElastiCache cluster: ${ex.getMessage}")
    } finally {
      elasticacheClient.shutdown()
    }
  }
}

object ElastiCacheLaunchQuestion {
  val CLUSTER_ID = "clusterId"
  val CACHE_NODE_TYPE = "cacheNodeType"
  val NUM_CACHE_NODES = "numCacheNodes"
}

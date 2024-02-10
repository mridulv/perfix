package io.perfix.question.redis

import com.amazonaws.auth.{AWSCredentials, AWSStaticCredentialsProvider, DefaultAWSCredentialsProviderChain}
import com.amazonaws.regions.Regions
import com.amazonaws.services.elasticache.{AmazonElastiCache, AmazonElastiCacheClientBuilder}
import com.amazonaws.services.elasticache.model._
import io.perfix.launch.{AWSCloudCredentials, LaunchStoreQuestion}
import io.perfix.model.{IntType, QuestionType, StringType}
import io.perfix.question.Question.QuestionLabel
import io.perfix.question.redis.ElastiCacheLaunchQuestion._
import io.perfix.stores.redis.RedisParams

import java.util.concurrent.TimeUnit

class RedisLaunchQuestion(override val credentials: AWSCloudCredentials,
                          override val storeQuestionParams: RedisParams) extends LaunchStoreQuestion {

  override val mapping: Map[QuestionLabel, QuestionType] = Map(
    CLUSTER_ID -> QuestionType(StringType),
    CACHE_NODE_TYPE -> QuestionType(StringType),
    NUM_CACHE_NODES -> QuestionType(IntType, isRequired = false)
  )

  override def setAnswers(answers: Map[QuestionLabel, Any]): Unit = {
    val clusterId = answers(CLUSTER_ID).toString
    val cacheNodeType = answers(CACHE_NODE_TYPE).toString
    val numCacheNodes = answers.get(NUM_CACHE_NODES).map(_.toString.toInt).getOrElse(1)

    val credentialsProvider = new AWSStaticCredentialsProvider(new AWSCredentials {
      override def getAWSAccessKeyId: String = credentials.accessKey.get
      override def getAWSSecretKey: String = credentials.accessSecret.get
    })

    val elasticacheClient = AmazonElastiCacheClientBuilder.standard()
      .withCredentials(credentialsProvider)
      .withRegion(Regions.US_EAST_1)
      .build()

    val createCacheClusterRequest = new CreateCacheClusterRequest()
      .withCacheClusterId(clusterId)
      .withCacheNodeType(cacheNodeType)
      .withEngine("redis")
      .withNumCacheNodes(numCacheNodes)
      .withPreferredAvailabilityZone("us-east-1a")

    try {
      elasticacheClient.createCacheCluster(createCacheClusterRequest)
      waitForCluster(elasticacheClient, clusterId)

      val describeRequest = new DescribeCacheClustersRequest()
        .withCacheClusterId(clusterId)
        .withShowCacheNodeInfo(true)
      val describeResponse = elasticacheClient.describeCacheClusters(describeRequest)
      val cacheNode = describeResponse.getCacheClusters.get(0).getCacheNodes.get(0)
      val endpoint = cacheNode.getEndpoint

      storeQuestionParams.url = Some(endpoint.getAddress)
      storeQuestionParams.port = Some(endpoint.getPort)

      println(s"Redis cluster creation initiated: ${describeResponse.getCacheClusters.get(0).getCacheClusterId}")
    } catch {
      case ex: Exception =>
        println(s"Error creating ElastiCache cluster: ${ex.getMessage}")
    } finally {
      elasticacheClient.shutdown()
    }
  }

  // Function to wait for the ElastiCache cluster to become available
  def waitForCluster(elasticacheClient: AmazonElastiCache, clusterId: String): Boolean = {
    var isAvailable = false
    var attempts = 0
    val maxAttempts = 30

    while (!isAvailable && attempts < maxAttempts) {
      try {
        TimeUnit.SECONDS.sleep(20) // Wait for 20 seconds before checking the status again
        val describeRequest = new DescribeCacheClustersRequest().withCacheClusterId(clusterId).withShowCacheNodeInfo(true)
        val describeResponse = elasticacheClient.describeCacheClusters(describeRequest)
        val clusterStatus = describeResponse.getCacheClusters.get(0).getCacheClusterStatus

        if (clusterStatus == "available") {
          isAvailable = true
        }
      } catch {
        case e: Exception => e.printStackTrace()
      }
      attempts += 1
    }
    isAvailable
  }
}

object ElastiCacheLaunchQuestion {
  val CLUSTER_ID = "clusterId"
  val CACHE_NODE_TYPE = "cacheNodeType"
  val NUM_CACHE_NODES = "numCacheNodes"
}

package io.perfix.forms.redis

import com.amazonaws.auth.{AWSCredentials, AWSStaticCredentialsProvider, DefaultAWSCredentialsProviderChain}
import com.amazonaws.regions.Regions
import com.amazonaws.services.elasticache.{AmazonElastiCache, AmazonElastiCacheClientBuilder}
import com.amazonaws.services.elasticache.model._
import io.perfix.common.CommonConfig.DB_SUBNET_GROUP_NAME
import io.perfix.launch.{AWSCloudParams, LaunchStoreForm}
import io.perfix.model.{IntType, FormInputType, StringType}
import io.perfix.forms.Form.FormInputName
import io.perfix.forms.redis.RedisLaunchForm._
import io.perfix.stores.redis.{RedisConnectionParams, RedisParams}

import java.util.concurrent.TimeUnit
import scala.util.Random

class RedisLaunchForm(override val credentials: AWSCloudParams,
                      override val formParams: RedisParams) extends LaunchStoreForm {

  override val launchFormInputMapping: Map[FormInputName, FormInputType] = Map(
    CACHE_NODE_TYPE -> FormInputType(StringType, isRequired = false),
    NUM_CACHE_NODES -> FormInputType(IntType, isRequired = false)
  )

  override def setAnswers(answers: Map[FormInputName, Any]): Unit = {
    val clusterId = "cluster" + Random.alphanumeric.take(5).mkString("")
    val cacheNodeType = answers.get(CACHE_NODE_TYPE).map(_.toString).getOrElse("cache.t3.micro")
    val numCacheNodes = answers.get(NUM_CACHE_NODES).map(_.toString.toInt).getOrElse(1)

    val credentialsProvider = if (credentials.useInstanceRole) {
      DefaultAWSCredentialsProviderChain.getInstance()
    } else {
      new AWSStaticCredentialsProvider(new AWSCredentials {
        override def getAWSAccessKeyId: String = credentials.accessKey.get
        override def getAWSSecretKey: String = credentials.accessSecret.get
      })
    }

    val elasticacheClient = AmazonElastiCacheClientBuilder.standard()
      .withCredentials(credentialsProvider)
      .withRegion(Regions.US_WEST_2)
      .build()

    val createCacheClusterRequest = new CreateCacheClusterRequest()
      .withCacheClusterId(clusterId)
      .withCacheNodeType(cacheNodeType)
      .withEngine("redis")
      .withNumCacheNodes(numCacheNodes)
      .withPreferredAvailabilityZone("us-west-2b")
      .withCacheSubnetGroupName(DB_SUBNET_GROUP_NAME)

    try {
      elasticacheClient.createCacheCluster(createCacheClusterRequest)
      waitForCluster(elasticacheClient, clusterId)

      val describeRequest = new DescribeCacheClustersRequest()
        .withCacheClusterId(clusterId)
        .withShowCacheNodeInfo(true)
      val describeResponse = elasticacheClient.describeCacheClusters(describeRequest)
      val cacheNode = describeResponse.getCacheClusters.get(0).getCacheNodes.get(0)
      val endpoint = cacheNode.getEndpoint

      formParams.redisConnectionParams = Some(RedisConnectionParams(endpoint.getAddress, endpoint.getPort))

      println(s"Redis cluster creation initiated: ${describeResponse.getCacheClusters.get(0).getCacheClusterId}")
    } catch {
      case ex: Exception =>
        println(s"Error creating ElastiCache cluster: ${ex.getMessage}")
    } finally {
      elasticacheClient.shutdown()
    }
  }

  def waitForCluster(elasticacheClient: AmazonElastiCache, clusterId: String): Boolean = {
    var isAvailable = false
    var attempts = 0
    val maxAttempts = 30

    while (!isAvailable && attempts < maxAttempts) {
      try {
        TimeUnit.SECONDS.sleep(20)
        val describeRequest = new DescribeCacheClustersRequest().withCacheClusterId(clusterId).withShowCacheNodeInfo(true)
        val describeResponse = elasticacheClient.describeCacheClusters(describeRequest)
        val clusterStatus = describeResponse.getCacheClusters.get(0).getCacheClusterStatus
        println("Waiting for the redis store to be up ...")

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

object RedisLaunchForm {
  val CLUSTER_ID = "clusterId"
  val CACHE_NODE_TYPE = "cacheNodeType"
  val NUM_CACHE_NODES = "numCacheNodes"
}

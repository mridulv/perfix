package io.perfix.question.documentdb

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.services.docdb.AmazonDocDBClientBuilder
import com.amazonaws.services.docdb.model._
import io.perfix.launch.{AWSCloudCredentials, LaunchStoreQuestion}
import io.perfix.model.{IntType, QuestionType, StringType}
import io.perfix.question.Question.QuestionLabel
import io.perfix.question.documentdb.DocumentDBLaunchQuestion._
import io.perfix.stores.documentdb.{DocumentDBConnectionParams, DocumentDBParams}

import scala.util.Random

class DocumentDBLaunchQuestion(override val credentials: AWSCloudCredentials,
                               override val storeQuestionParams: DocumentDBParams) extends LaunchStoreQuestion {

  override val mapping: Map[QuestionLabel, QuestionType] = Map(
    DB_CLUSTER_IDENTIFIER -> QuestionType(StringType),
    MASTER_USERNAME -> QuestionType(StringType),
    MASTER_PASSWORD -> QuestionType(StringType, isRequired = false),
    INSTANCE_CLASS -> QuestionType(StringType),
    STORAGE_CAPACITY -> QuestionType(IntType)
  )

  override def setAnswers(answers: Map[QuestionLabel, Any]): Unit = {
    val clusterIdentifier = answers(DB_CLUSTER_IDENTIFIER).toString
    val masterUsername = answers(MASTER_USERNAME).toString
    val masterPassword = answers.get(MASTER_PASSWORD).map(_.toString).getOrElse(Random.alphanumeric.take(10).mkString(""))
    val instanceClass = answers(INSTANCE_CLASS).toString

    val docDBClient = AmazonDocDBClientBuilder.standard()
      .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
      .withRegion("us-east-1")
      .build()

    val createDBClusterRequest = new CreateDBClusterRequest()
      .withDBClusterIdentifier(clusterIdentifier)
      .withMasterUsername(masterUsername)
      .withMasterUserPassword(masterPassword)
      .withEngineVersion("4.0.0")
      .withDBClusterParameterGroupName("default.docdb4.0")
      .withVpcSecurityGroupIds("your-vpc-security-group-id")
      .withAvailabilityZones("us-east-1a")

    val createDBInstanceRequest = new CreateDBInstanceRequest()
      .withDBInstanceIdentifier(clusterIdentifier + "-instance")
      .withDBClusterIdentifier(clusterIdentifier)
      .withDBInstanceClass(instanceClass)
      .withAvailabilityZone("us-east-1a")

    try {
      val clusterResponse = docDBClient.createDBCluster(createDBClusterRequest)
      val instanceResponse = docDBClient.createDBInstance(createDBInstanceRequest)

      val connectionParams = DocumentDBConnectionParams(clusterResponse.getEndpoint + ":" + clusterResponse.getPort, "")
      storeQuestionParams.documentDBConnectionParams = Some(connectionParams)

      println(s"DocumentDB cluster creation initiated: ${clusterResponse.getDBClusterIdentifier}")
      println(s"DocumentDB instance creation initiated: ${instanceResponse.getDBInstanceIdentifier}")
    } catch {
      case ex: Exception =>
        println(s"Error creating DocumentDB cluster/instance: ${ex.getMessage}")
    } finally {
      docDBClient.shutdown()
    }
  }
}

object DocumentDBLaunchQuestion {
  val DB_CLUSTER_IDENTIFIER = "dbClusterIdentifier"
  val MASTER_USERNAME = "masterUsername"
  val MASTER_PASSWORD = "masterPassword"
  val INSTANCE_CLASS = "instanceClass"
  val STORAGE_CAPACITY = "storageCapacity"
}


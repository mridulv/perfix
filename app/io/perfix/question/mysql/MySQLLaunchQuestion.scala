package io.perfix.question.mysql

import com.amazonaws.auth.{AWSCredentials, AWSStaticCredentialsProvider, DefaultAWSCredentialsProviderChain}
import com.amazonaws.services.rds.{AmazonRDS, AmazonRDSClientBuilder}
import com.amazonaws.services.rds.model.{CreateDBInstanceRequest, DBInstance, DescribeDBInstancesRequest}
import io.perfix.launch.{AWSCloudParams, LaunchStoreQuestion}
import io.perfix.model.{QuestionType, StringType}
import io.perfix.question.Question.QuestionLabel
import io.perfix.question.mysql.ConnectionParamsQuestion.{PASSWORD, USERNAME}
import io.perfix.question.mysql.MySQLLaunchQuestion._
import io.perfix.question.mysql.TableParamsQuestions.DBNAME
import io.perfix.stores.mysql.{MySQLConnectionParams, MySQLParams, MySQLTableParams}

import java.util.concurrent.TimeUnit
import scala.util.Random

class MySQLLaunchQuestion(override val credentials: AWSCloudParams,
                          override val storeQuestionParams: MySQLParams) extends LaunchStoreQuestion {

  override val mapping: Map[QuestionLabel, QuestionType] = Map(
    DBNAME -> QuestionType(StringType),
    USERNAME -> QuestionType(StringType),
    PASSWORD -> QuestionType(StringType, isRequired = false),
    INSTANCE_IDENTIFIER -> QuestionType(StringType),
    INSTANCE_TYPE -> QuestionType(StringType)
  )

  override def setAnswers(answers: Map[QuestionLabel, Any]): Unit = {
    val pwd = Random.alphanumeric.take(10).mkString("")
    val dbName = answers(DBNAME).toString
    val username = answers(USERNAME).toString
    val password = answers.get(PASSWORD).map(_.toString).getOrElse(pwd)
    val instanceIdentifier = answers(INSTANCE_IDENTIFIER).toString
    val instanceType = answers(INSTANCE_TYPE).toString

    val credentialsProvider = if (credentials.useInstanceRole) {
      DefaultAWSCredentialsProviderChain.getInstance()
    } else {
      new AWSStaticCredentialsProvider(new AWSCredentials {
        override def getAWSAccessKeyId: String = credentials.accessKey.get
        override def getAWSSecretKey: String = credentials.accessSecret.get
      })
    }

    val rdsClient = AmazonRDSClientBuilder.standard()
      .withCredentials(credentialsProvider)
      .withRegion("us-east-1")
      .build()

    val createDBRequest = new CreateDBInstanceRequest()
      .withDBInstanceIdentifier(instanceIdentifier)
      .withDBInstanceClass(instanceType)
      .withEngine("mysql")
      .withMasterUsername(username)
      .withMasterUserPassword(password)
      .withAllocatedStorage(20)
      .withDBName(dbName)
      .withAvailabilityZone("us-east-1a")

    try {
      val response = rdsClient.createDBInstance(createDBRequest)
      waitForInstance(rdsClient, instanceIdentifier)

      createTransitGateway(response.getDBSubnetGroup.getVpcId)

      val connectUrl = s"jdbc:mysql://${response.getEndpoint.getAddress}:${response.getEndpoint.getPort}/${response.getDBName}?user=${username}&password=${password}"
      storeQuestionParams.mySQLConnectionParams = Some(MySQLConnectionParams(connectUrl, username, password))
      storeQuestionParams.mySQLTableParams = Some(MySQLTableParams(dbName, s"test${Random.alphanumeric.take(5).mkString("")}"))
      println(s"RDS instance creation initiated: ${response.getDBInstanceIdentifier}")
    } catch {
      case ex: Exception =>
        println(s"Error creating RDS instance: ${ex.getMessage}")
    } finally {
      rdsClient.shutdown()
    }
  }

  def waitForInstance(rdsClient: AmazonRDS, instanceIdentifier: String): Option[DBInstance] = {
    var instance: Option[DBInstance] = None
    var status = ""
    val maxRetries = 30
    var retries = 0

    while (status != "available" && retries < maxRetries) {
      try {
        TimeUnit.SECONDS.sleep(20)
        val describeDBInstancesRequest = new DescribeDBInstancesRequest().withDBInstanceIdentifier(instanceIdentifier)
        val response = rdsClient.describeDBInstances(describeDBInstancesRequest)
        if (!response.getDBInstances.isEmpty) {
          val dbInstance = response.getDBInstances.get(0)
          status = dbInstance.getDBInstanceStatus
          instance = Some(dbInstance)
        }
      } catch {
        case e: Exception => e.printStackTrace()
      }
      retries += 1
    }

    instance
  }
}

object MySQLLaunchQuestion {
  val INSTANCE_IDENTIFIER = "instanceIdentifier"
  val INSTANCE_TYPE = "instanceType"
}

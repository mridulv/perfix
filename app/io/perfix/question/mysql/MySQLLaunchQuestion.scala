package io.perfix.question.mysql

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.services.rds.AmazonRDSClientBuilder
import com.amazonaws.services.rds.model.CreateDBInstanceRequest
import io.perfix.launch.{AWSCloudCredentials, LaunchStoreQuestion}
import io.perfix.model.{QuestionType, StringType}
import io.perfix.question.Question.QuestionLabel
import io.perfix.question.mysql.MySQLLaunchQuestion._
import io.perfix.stores.mysql.{MySQLConnectionParams, MySQLParams, MySQLTableParams}

import scala.util.Random

class MySQLLaunchQuestion(override val credentials: AWSCloudCredentials,
                          override val storeQuestionParams: MySQLParams) extends LaunchStoreQuestion {

  override val mapping: Map[QuestionLabel, QuestionType] = Map(
    DB_NAME -> QuestionType(StringType),
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

    val rdsClient = AmazonRDSClientBuilder.standard()
      .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
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
      val connectUrl = s"jdbc:mysql://${response.getDBInstanceIdentifier}:${response.getDbInstancePort}/${response.getDBName}?user=${username}&password=${password}"
      storeQuestionParams.mySQLConnectionParams = Some(MySQLConnectionParams(connectUrl, username, password))
      storeQuestionParams.mySQLTableParams = Some(MySQLTableParams(dbName, ""))
      println(s"RDS instance creation initiated: ${response.getDBInstanceIdentifier}")
    } catch {
      case ex: Exception =>
        println(s"Error creating RDS instance: ${ex.getMessage}")
    } finally {
      rdsClient.shutdown()
    }
  }
}

object MySQLLaunchQuestion {
  val DB_NAME = "dbName"
  val USERNAME = "username"
  val PASSWORD = "password"
  val INSTANCE_IDENTIFIER = "instanceIdentifier"
  val INSTANCE_TYPE = "instanceType"
}

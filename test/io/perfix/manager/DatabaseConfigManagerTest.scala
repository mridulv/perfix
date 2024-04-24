package io.perfix.manager

import io.perfix.forms.AWSCloudParamsForm.{AWS_ACCESS_KEY, AWS_ACCESS_SECRET, LAUNCH_DB}
import io.perfix.forms.Form
import io.perfix.forms.mysql.MySQLConnectionParamsForm.{PASSWORD, URL, USERNAME}
import io.perfix.forms.mysql.MySQLLaunchForm.{INSTANCE_IDENTIFIER, INSTANCE_TYPE}
import io.perfix.forms.mysql.MySQLTableIndicesDetailForm.SECONDARY_INDEX_COLUMNS
import io.perfix.forms.mysql.MySQLTableParamsForm.{DBNAME, TABLENAME}
import io.perfix.forms.redis.RedisLaunchForm.CLUSTER_ID
import io.perfix.model.{DatabaseConfigParams, FormInputValue, FormInputValues, FormInputs}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class DatabaseConfigManagerTest extends AnyFlatSpec with Matchers {
  val mappedVariables: Map[String, Any] = Map(
    USERNAME -> "root",
    URL -> "jdbc:mysql://localhost:3306/perfix",
    PASSWORD -> "test12345",
    DBNAME -> "perfix",
    TABLENAME -> "test",
    SECONDARY_INDEX_COLUMNS -> "student_name",
    INSTANCE_IDENTIFIER -> "dbinstance",
    INSTANCE_TYPE -> "db.t4g.micro",
    AWS_ACCESS_KEY -> "************",
    AWS_ACCESS_SECRET -> "************************************",
    LAUNCH_DB -> false
  )

  "DatabaseConfigManager" should "allow getting form inputs and submitting form inputs" in {
    val databaseConfigManager = new DatabaseConfigManager
    val configParams = DatabaseConfigParams(name = "test-params", storeName = "mysql")
    val configId = databaseConfigManager.create(databaseConfigParams = configParams)
    var inputs = databaseConfigManager.currentForm(configId)

    while (inputs.isDefined) {
      val answerMapping = inputs.getOrElse(FormInputs(inputs = Map.empty)).inputs.flatMap { case (k, formInputType) =>
        mappedVariables.get(k) match {
          case Some(v) => Some(FormInputValue(k, v))
          case None => None
        }
      }
      databaseConfigManager.submitForm(configId, FormInputValues(answerMapping.toSeq))
      inputs = databaseConfigManager.currentForm(configId)
    }

    databaseConfigManager.get(configId).formInputValues.isDefined shouldBe true
    databaseConfigManager.get(configId).formInputValues.get.find(_.inputName == USERNAME) shouldBe Some(FormInputValue(USERNAME, "root"))
    databaseConfigManager.get(configId).formInputValues.get.find(_.inputName == LAUNCH_DB) shouldBe Some(FormInputValue(LAUNCH_DB, false))
    databaseConfigManager.get(configId).formInputValues.get.find(_.inputName == CLUSTER_ID) shouldBe None
  }
}
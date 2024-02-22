package io.perfix.common

object CommonConfig {

  val EKS_NAME = "test-cluster"
  val EKS_REGION = "us-west-2"
  val DB_SUBNET_GROUP_NAME = "DBSubnet"
  val IS_TRIAL_MODE: Boolean = Option(System.getenv("MODE")).map(_.toLowerCase()) match {
    case Some("trial") => true
    case _ => false
  }

}

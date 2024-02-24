package io.perfix.common

object CommonConfig {

  val EKS_NAME: String = Option(System.getenv("EKS_NAME")).map(_.toLowerCase()).getOrElse("test-cluster")
  val EKS_REGION: String = Option(System.getenv("EKS_REGION")).map(_.toLowerCase()).getOrElse("us-west-2")
  val DB_SUBNET_GROUP_NAME: String = Option(System.getenv("DB_SUBNET_GROUP_NAME")).map(_.toLowerCase()).getOrElse("DBSubnet")
  val IS_TRIAL_MODE: Boolean = Option(System.getenv("MODE")).map(_.toLowerCase()) match {
    case Some("trial") => true
    case _ => false
  }

}

package io.perfix.common

object CommonConfig {

  val DB_SUBNET_GROUP_NAME: String = Option(System.getenv("DB_SUBNET_GROUP_NAME")).map(_.toLowerCase()).getOrElse("dbsubnet")
  val IS_TRIAL_MODE: Boolean = Option(System.getenv("MODE")).map(_.toLowerCase()) match {
    case Some("trial") => true
    case _ => false
  }

}

import Dependencies._
import sbt.Keys._
import play.sbt.PlaySettings

ThisBuild / scalaVersion     := "2.13.12"
ThisBuild / organization     := "com.perfix"
ThisBuild / organizationName := "perfix"

resolvers += "Typesafe repo" at "https://repo.typesafe.com/typesafe/releases/"

version := sys.env.get("TAG").filter(_.nonEmpty).getOrElse("latest")

dockerRepository := Some("mridulverma")

lazy val root = (project in file("."))
  .enablePlugins(PlayJava)
  .enablePlugins(DockerPlugin)
  .settings(
        name := "perfix",
        libraryDependencies += munit % Test,
        libraryDependencies += guice,
        libraryDependencies += "com.github.javafaker" % "javafaker" % "1.0.2",
        libraryDependencies += "mysql" % "mysql-connector-java" % "8.0.33",
        libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.9" % Test,
        libraryDependencies += "com.amazonaws" % "aws-java-sdk-dynamodb" % "1.12.618",
        libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test,
        libraryDependencies += "ai.x" %% "play-json-extensions" % "0.42.0",
        libraryDependencies += "redis.clients" % "jedis" % "5.2.0-alpha2",
        libraryDependencies += "org.mockito" % "mockito-core" % "2.8.47" % "test",
        libraryDependencies += "org.mongodb" % "mongo-java-driver" % "3.12.12",
        libraryDependencies += "com.amazonaws" % "aws-java-sdk" % "1.12.115"
  )


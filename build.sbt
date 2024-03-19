import Dependencies._
import sbt.Keys._
import play.sbt.PlaySettings

ThisBuild / scalaVersion     := "2.13.12"
ThisBuild / organization     := "com.perfix"
ThisBuild / organizationName := "perfix"

resolvers += "Typesafe repo" at "https://repo.typesafe.com/typesafe/releases/"

version := sys.env.get("TAG").filter(_.nonEmpty).getOrElse("latest")

dockerRepository := Some("mridulverma")
dockerUpdateLatest := true
dockerBuildxPlatforms := Seq("linux/arm64/v8", "linux/amd64")

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
          libraryDependencies += "com.amazonaws" % "aws-java-sdk" % "1.12.115",
          libraryDependencies += "info.picocli" % "picocli" % "4.7.5",
          libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.9" % Test,
          libraryDependencies += "org.mockito" %% "mockito-scala" % "1.16.42" % Test,
          libraryDependencies += "com.h2database" % "h2" % "1.4.200" % Test,
          dockerBaseImage := "openjdk:11-jre-slim"
  )

Universal / javaOptions ++= Seq(
        "-Dpidfile.path=/dev/null"
)

dockerBuildCommand := {
  if (sys.props("os.arch") != "amd64") {
    // use buildx with platform to build supported amd64 images on other CPU architectures
    // this may require that you have first run 'docker buildx create' to set docker buildx up
    dockerExecCommand.value ++ Seq("buildx", "build", "--platform=linux/amd64", "--load") ++ dockerBuildOptions.value :+ "."
  } else dockerBuildCommand.value
}

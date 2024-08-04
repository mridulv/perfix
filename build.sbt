import Dependencies.*
import com.typesafe.sbt.packager.docker.Cmd
import sbt.Keys.{scalacOptions, *}

ThisBuild / scalaVersion     := "2.13.12"
ThisBuild / organization     := "com.perfix"
ThisBuild / organizationName := "perfix"

version := sys.env.get("TAG").filter(_.nonEmpty).getOrElse("latest")

dockerRepository := Some("mridulverma")
dockerUpdateLatest := true
dockerBuildxPlatforms := Seq("linux/arm64/v8", "linux/amd64")

val playPac4jVersion = "11.1.0-PLAY2.8"
val pac4jVersion = "5.7.0"

lazy val root = (project in file("."))
  .enablePlugins(PlayJava)
  .enablePlugins(DockerPlugin)
  .settings(
    name := "perfix",
    libraryDependencies += munit % Test,
    libraryDependencies += guice,
    libraryDependencies += "com.github.javafaker" % "javafaker" % "1.0.2",
    libraryDependencies += "mysql" % "mysql-connector-java" % "8.0.33",
    libraryDependencies += "org.mariadb.jdbc" % "mariadb-java-client" % "3.0.6",
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
    libraryDependencies += "org.postgresql" % "postgresql" % "42.3.1",
    libraryDependencies += "org.playframework" %% "play-slick" % "6.1.0",
    libraryDependencies += "org.playframework" %% "play-slick-evolutions" % "6.1.0",
    libraryDependencies += "org.pac4j" %% "play-pac4j" % playPac4jVersion,
    libraryDependencies += "org.pac4j" % "pac4j-oauth" % pac4jVersion,
    libraryDependencies += "com.typesafe.play" %% "play-cache" % "2.8.18",
    libraryDependencies += "io.cequence" %% "openai-scala-client" % "1.0.0",
    dependencyOverrides += "org.scala-lang.modules" %% "scala-xml" % "1.2.0",
    libraryDependencies += "org.apache.shiro" % "shiro-crypto-cipher" % "1.13.0",
    libraryDependencies += "com.github.jsqlparser" % "jsqlparser" % "4.5",
    dockerBaseImage := "openjdk:11-jre-slim",
    semanticdbEnabled := true,
    scalacOptions += "-Wunused:imports",
    // Additional Docker settings
    Docker / daemonUser := "root", // Run the container as root user
    Docker / dockerCommands ++= Seq(
      Cmd("USER", "root"),
      Cmd("RUN", "apt-get update && apt-get install -y sudo"),
      Cmd("RUN", "useradd -m myuser && echo 'myuser ALL=(ALL) NOPASSWD:ALL' >> /etc/sudoers"),
      Cmd("USER", "myuser")
    )

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

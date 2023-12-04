import Dependencies._

ThisBuild / scalaVersion     := "2.13.12"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
      name := "perfix",
      libraryDependencies += munit % Test,
      libraryDependencies += "com.github.javafaker" % "javafaker" % "1.0.2",
      libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.44",
      libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.9" % Test
)


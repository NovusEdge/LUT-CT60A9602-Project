import Dependencies._

ThisBuild / scalaVersion     := "2.13.12"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
    name := "lut-ct60a9602-project",
    libraryDependencies += munit % Test
  )

libraryDependencies += "com.lihaoyi" %% "requests" % "0.9.0"
libraryDependencies += "com.lihaoyi" %% "upickle" % "4.1.0"

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.

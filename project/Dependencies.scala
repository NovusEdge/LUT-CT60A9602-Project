import sbt._

/**
 * Defines the dependencies used in the project.
 * 
 * This object contains the SBT library dependencies required for the project,
 * including test frameworks and other external libraries.
 */
object Dependencies {
  /** MUnit testing framework dependency */
  lazy val munit = "org.scalameta" %% "munit" % "0.7.29"
  lazy val ujson = "com.lihaoyi" %% "ujson" % "0.7.29"
}

import sbt._
import Keys._

scalaVersion := "2.12.4"

val slf4jVersion = "1.7.25"
val logBackVersion = "1.2.3"
val scalaLoggingVersion = "3.7.2"
//val json4sVersion = "3.2.11"

/*
 * Logging Stack
 */
val slf4jApi = "org.slf4j" % "slf4j-api" % slf4jVersion
val logBackClassic = "ch.qos.logback" % "logback-classic" % logBackVersion
val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion
val loggingStack = Seq(slf4jApi, logBackClassic, scalaLogging)

/*
 * Unit Testing Stack
 */
val mockito = "org.mockito" % "mockito-all" % "2.13.0" % "test"
val scalatest = "org.scalatest" %% "scalatest" % "3.0.4" % "test"
val unitTestingStack = Seq(mockito, scalatest)

val commonDependencies = unitTestingStack ++ loggingStack

name := "experibot"

// factor out common settings into a sequence
lazy val commonSettings = Seq( 
  organization := "com.arxality",
  version := "0.0.1-SNAPSHOT",
  scalaVersion := "2.11.7",
  scalacOptions ++= Seq("-unchecked", "-deprecation"),
  libraryDependencies ++= commonDependencies
)


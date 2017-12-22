import sbt._
import Keys._

scalaVersion := "2.12.4"
name := "experibot"
organization := "com.arxality"
version := "0.0.1-SNAPSHOT"

scalacOptions ++= Seq("-unchecked", "-deprecation")

val slf4jVersion = "1.7.25"
val logBackVersion = "1.2.3"
//val logBackMongoVersion = "0.1.5"
val scalaLoggingVersion = "3.7.2"
//val json4sVersion = "3.2.11"
val mongoVersion = "3.6.1"

/*
 * Logging Stack
 */
val slf4jApi = "org.slf4j" % "slf4j-api" % slf4jVersion
val logBackClassic = "ch.qos.logback" % "logback-classic" % logBackVersion
val logBackCore = "ch.qos.logback" % "logback-core" % logBackVersion
val logBackAccess = "ch.qos.logback" % "logback-access" % logBackVersion
//val logBackMongoCore = "ch.qos.logback.contrib" % "logback-mongodb-core" % logBackMongoVersion
//val logBackMongoAccess = "ch.qos.logback.contrib" % "logback-mongodb-access" % logBackMongoVersion
val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion
val loggingStack = Seq(
    slf4jApi, 
    logBackCore, 
    logBackAccess, 
    logBackClassic, 
//    logBackMongoCore,
//    logBackMongoAccess, 
    scalaLogging)

/*
 * Database Stack
 */
 val mongoDriver = "org.mongodb" % "mongodb-driver" % mongoVersion
 val databaseStack = Seq ( mongoDriver )

/*
 * Unit Testing Stack
 */
val mockito = "org.mockito" % "mockito-core" % "2.13.0" % "test"
val scalatest = "org.scalatest" %% "scalatest" % "3.0.4" % "test"
val unitTestingStack = Seq(mockito, scalatest)

libraryDependencies ++= unitTestingStack
libraryDependencies ++= loggingStack
libraryDependencies ++= databaseStack

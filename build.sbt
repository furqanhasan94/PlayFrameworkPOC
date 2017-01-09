name := """play-java-intro"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava,PlayEbean)

scalaVersion := "2.11.8"

libraryDependencies += "org.mockito" % "mockito-core" % "2.1.0"

libraryDependencies += javaWs % "test"

libraryDependencies += javaWs 

libraryDependencies += "org.avaje.ebean" % "ebean" % "9.5.1"

libraryDependencies += "com.google.code.gson" % "gson" % "2.8.0"

libraryDependencies += "org.json" % "json" % "20090211"




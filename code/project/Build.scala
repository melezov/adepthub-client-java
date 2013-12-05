import sbt._
import Keys._

// Eclipse plugin
import com.typesafe.sbteclipse.plugin.EclipsePlugin._

// Dependency graph plugin
import net.virtualvoid.sbt.graph.Plugin._

// Assembly plugin
import sbtassembly.Plugin._
import AssemblyKeys._

// ----------------------------------------------------------------------------

object Default {
  val settings =
    Defaults.defaultSettings ++
    eclipseSettings ++
    assemblySettings ++
    graphSettings ++ Seq(
      organization := "com.dslplatform"

    , crossPaths := false
    , autoScalaLibrary := false
    , scalaVersion := "2.10.3"

    , javaHome := sys.env.get("JDK16_HOME").map(file(_))
    , javacOptions := Seq(
        "-deprecation"
      , "-encoding", "UTF-8"
      , "-Xlint:unchecked"
      , "-source", "1.6"
      , "-target", "1.6"
      )
			
    , unmanagedSourceDirectories in Compile := (javaSource in Compile).value :: Nil
    , unmanagedSourceDirectories in Test := Nil

    , publishArtifact in (Compile, packageDoc) := false

    , EclipseKeys.projectFlavor := EclipseProjectFlavor.Java
    , EclipseKeys.createSrc := EclipseCreateSrc.Default + EclipseCreateSrc.Resource
    )
}

// ----------------------------------------------------------------------------

object Dependencies {
  val slf4j       = "org.slf4j" % "slf4j-api" % "1.7.5" 
	val slf4jSimple = "org.slf4j" % "slf4j-simple" % "1.7.5"
	
	val minimalJson = "com.eclipsesource.minimal-json" % "minimal-json" % "0.9.1"
}

// ----------------------------------------------------------------------------

object Build extends Build {
  import Default._
  import Dependencies._

  lazy val api = Project(
    "api"
  , file("api")
  , settings = Default.settings ++ Seq(
      name := "Adepthub-Client-Java-API"
    , libraryDependencies ++= Seq(
		    slf4jSimple
			, minimalJson
      )
    )
  )

  lazy val cmdline = Project(
    "cmdline"
  , file("cmdline")
  , settings = Default.settings ++ Seq(
      name := "Adepthub-Client-Java-Cmdline"
    , libraryDependencies ++= Seq(
      )
	  )
  ) dependsOn(api)
}

import sbt._
import Keys._

lazy val compilerOptions = Seq(
  "-target:jvm-1.8",
  "-deprecation",
  "-feature",
  "-unchecked",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-unused-import",
  "-Xfuture"
)

lazy val commonSettings = Defaults.coreDefaultSettings ++ Seq(
  organization  := "com.snowplowanalytics",
  version       := "0.1.0-SNAPSHOT",
  scalaVersion  := "2.11.12",
  javacOptions  ++= Seq("-source", "1.8", "-target", "1.8"),
  scalacOptions ++= compilerOptions,
   scalacOptions in (Compile, console) ~= {
    _.filterNot(Set("-Ywarn-unused-import"))
  },
  scalacOptions in (Test, console) ~= {
    _.filterNot(Set("-Ywarn-unused-import"))
  }
)

lazy val paradiseDependency =
  "org.scalamacros" % "paradise" % scalaMacrosVersion cross CrossVersion.full
lazy val macroSettings = Seq(
  libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value,
  addCompilerPlugin(paradiseDependency)
)

lazy val noPublishSettings = Seq(
  publish := {},
  publishLocal := {},
  publishArtifact := false
)

lazy val scioVersion = "0.5.2"
lazy val beamVersion = "2.4.0"
lazy val sceVersion = "0.32.0"
lazy val scalaMacrosVersion = "2.1.0"
lazy val slf4jVersion = "1.7.25"
lazy val scalatestVersion = "3.0.5"

lazy val root: Project = Project(
  "beam-enrich",
  file(".")
).settings(
  commonSettings ++ macroSettings ++ noPublishSettings,
  description := "Streaming enrich job written using SCIO",
  buildInfoKeys := Seq[BuildInfoKey](organization, name, version, "sceVersion" -> sceVersion),
  buildInfoPackage := "com.snowplowanalytics.snowplow.enrich.beam.generated",
  libraryDependencies ++= Seq(
    "com.spotify" %% "scio-core" % scioVersion,
    "org.apache.beam" % "beam-runners-google-cloud-dataflow-java" % beamVersion,
    "com.snowplowanalytics" %% "snowplow-common-enrich" % sceVersion,
    "org.slf4j" % "slf4j-simple" % slf4jVersion
  ) ++ Seq(
    "com.spotify" %% "scio-test" % scioVersion,
    "org.scalatest" %% "scalatest" % scalatestVersion
  ).map(_ % "test")
).enablePlugins(PackPlugin, BuildInfoPlugin)

lazy val repl: Project = Project(
  "repl",
  file(".repl")
).settings(
  commonSettings ++ macroSettings ++ noPublishSettings,
  description := "Scio REPL for beam-enrich",
  libraryDependencies ++= Seq(
    "com.spotify" %% "scio-repl" % scioVersion
  ),
  mainClass in Compile := Some("com.spotify.scio.repl.ScioShell")
).dependsOn(
  root
)

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

// ThisBuild / scalacOptions += "-Ypartial-unification"

lazy val root = (project in file("."))
  .settings(
    name := "vampire-learning-scala",
    idePackagePrefix := Some("vampire.learning.scala"),
    libraryDependencies := Seq(
      "org.typelevel" %% "cats-core" % "2.9.0",
      "org.typelevel" %% "cats-effect" % "2.5.3",
      "org.polyvariant" %% "colorize" % "0.3.2"
    )
  )

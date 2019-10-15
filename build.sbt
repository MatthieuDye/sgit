name := "sgit"

version := "0.1"

scalaVersion := "2.13.1"

lazy val sgit = (project in file("."))
  .enablePlugins(JavaAppPackaging)
  .settings(
    name := "SGit",
    libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.8",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.8" % Test,
    libraryDependencies += "com.github.pathikrit" %% "better-files" % "3.8.0",
    libraryDependencies += "org.apache.commons" % "commons-lang3" % "3.9",
    libraryDependencies += "com.outr" %% "hasher" % "1.2.2"


)
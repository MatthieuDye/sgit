name := "sgit"

version := "0.1"

scalaVersion := "2.13.1"

Test / parallelExecution := false
Test / fork := true
Test / baseDirectory := file(System.getProperty("user.dir")+"\\tests")


lazy val sgit = (project in file("."))
  .enablePlugins(JavaAppPackaging)
  .settings(
      maintainer:= "sgitscala@yopmail.com",
    name := "SGit",
    libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.8",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.8" % Test,
    libraryDependencies += "com.github.pathikrit" %% "better-files" % "3.8.0",
    libraryDependencies += "org.apache.commons" % "commons-lang3" % "3.9",
    libraryDependencies += "com.outr" %% "hasher" % "1.2.2",
    libraryDependencies += "org.fusesource.jansi" % "jansi" % "1.17.1",
    libraryDependencies += "com.github.scopt" %% "scopt" % "4.0.0-RC2"

  )

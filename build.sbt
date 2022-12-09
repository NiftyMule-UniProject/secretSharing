ThisBuild / version := "1.0.4"

ThisBuild / scalaVersion := "3.2.1"

lazy val root = (project in file("."))
  .settings(
    name := "secretSharing"
  )

libraryDependencies ++= Seq(
  "org.scalanlp" %% "breeze" % "2.1.0",
  "com.novocode" % "junit-interface" % "0.11" % "test",
  "org.scalatest" %% "scalatest" % "3.2.14" % Test,
  "org.bouncycastle" % "bcprov-jdk15on" % "1.70",
  "org.bouncycastle" % "bcpkix-jdk15on" % "1.70"
)
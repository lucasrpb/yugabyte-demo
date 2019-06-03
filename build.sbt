name := "yugabyte-demo"

version := "0.1"

scalaVersion := "2.12.8"

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)

libraryDependencies ++= Seq(
  "org.scalactic" %% "scalactic" % "3.0.5",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test",
  "org.apache.commons" % "commons-lang3" % "3.8.1",
  "com.yugabyte" % "cassandra-driver-core" % "3.2.0-yb-12",
  "org.scala-lang.modules" % "scala-java8-compat_2.12" % "0.9.0"
)


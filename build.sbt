name := "contrast"

version := "0.0.1"

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
"com.typesafe"  %   "config"    % "1.2.1",
"org.scalaz" %% "scalaz-core" % "7.1.2",
"org.scalatest" %%  "scalatest" % "2.2.4" % "test"
)

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-Xfatal-warnings")

wartremoverErrors in (Compile, compile) ++= Warts.unsafe

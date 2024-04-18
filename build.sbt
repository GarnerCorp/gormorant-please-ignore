import Libraries._

val addScalacOptions = Seq(
)

val release = "https://nexus.garnercorp.com/repository/maven-releases"
val snapshot = "https://nexus.garnercorp.com/repository/maven-snapshots"
val proxy = "https://nexus.garnercorp.com/repository/maven-all"

val commonSettings = Seq(
  name := "gormorant",
  organization := "com.garnercorp",
  scalaVersion := "2.13.12",
  organizationName := "Garner",
  homepage := Some(url("https://github.com/ChristopherDavenport/cormorant")),
  licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
  developers := List(
    Developer(
      "ChristopherDavenport",
      "Christopher Davenport",
      "chris@christopherdavenport.tech",
      url("https://www.github.com/ChristopherDavenport")
    )
  ),
  Test / publishArtifact := true,
  publishMavenStyle := true,
  publishTo := {
    if (isSnapshot.value)
      Some("snapshots" at snapshot)
    else
      Some("releases" at release)
  },
  scalacOptions ++= addScalacOptions,
  addCompilerPlugin(
    "org.typelevel" %% "kind-projector" % "0.13.2" cross CrossVersion.full
  ),
  addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
  testFrameworks += new TestFramework("munit.Framework"),
  libraryDependencies ++= Seq(
    MUnitTest,
    MUnitCatsEffectTest,
    ScalaCheckEffectMUnit
  ),
  (sys.env.get("NEXUS_USER"), sys.env.get("NEXUS_PASSWORD")) match {
    case (Some(user), Some(password)) =>
      credentials += Credentials(
        "Sonatype Nexus Repository Manager",
        "nexus.garnercorp.com",
        user,
        password
      )
    case _ =>
      credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")
  },
  resolvers += "Nexus" at proxy
)

lazy val noPublish = Seq(
  publish := {},
  publishLocal := {},
  publishArtifact := false,
  publish / skip := true
)

lazy val cormorant = project
  .in(file("."))
  .disablePlugins(MimaPlugin)
  .aggregate(core, generic, parser, refined, fs2)
  .settings(commonSettings: _*)
  .settings(noPublish: _*)

lazy val core = project
  .in(file("modules/core"))
  .settings(commonSettings)
  .settings(
    name := "cormorant-core",
    libraryDependencies ++= Seq(
      CatsCore,
      CatsKernel
    )
  )

lazy val generic = project
  .in(file("modules/generic"))
  .settings(commonSettings)
  .dependsOn(core)
  .settings(
    name := "cormorant-generic",
    libraryDependencies ++= Seq(
      CatsCore,
      CatsKernel,
      Shapeless
    )
  )

lazy val parser = project
  .in(file("modules/parser"))
  .settings(commonSettings)
  .dependsOn(core % "compile;test->test")
  .settings(
    name := "cormorant-parser",
    libraryDependencies ++= Seq(
      AttoCore,
      CatsCore
    )
  )

lazy val refined = project
  .in(file("modules/refined"))
  .settings(commonSettings)
  .dependsOn(core)
  .settings(
    name := "cormorant-refined",
    libraryDependencies ++= Seq(
      CatsCore,
      Refined
    )
  )

lazy val fs2 = project
  .in(file("modules/fs2"))
  .settings(commonSettings)
  .dependsOn(core % "compile;test->test", parser)
  .settings(
    name := "cormorant-fs2",
    libraryDependencies ++= Seq(
      AttoCore,
      CatsCore,
      CatsKernel,
      fs2Core,
      fs2IOTest
    )
  )

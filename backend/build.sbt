val ScalaVersion = "2.13.14"
val Http4sVersion = "0.23.23"          // Последняя версия Http4s, поддерживающая Scala 2.12
val MunitVersion = "0.7.29"            // Поддержка MUnit для тестирования
val MunitCatsEffectVersion = "1.0.7"   // Совместимая версия для MUnit с Cats Effect
val LogbackVersion = "1.2.12"          // Устойчивый Logback для логирования
val CircleVersion = "0.14.6"
val DoobieVersion = "1.0.0-RC2"
val PostgresVersionLib = "42.6.0"

lazy val root = (project in file("."))
  .settings(
    organization := "com.simple.site",
    name := "Simple Forum",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := ScalaVersion,
    libraryDependencies ++= Seq(
      "org.http4s"      %% "http4s-ember-server" % Http4sVersion,
      "org.http4s"      %% "http4s-ember-client" % Http4sVersion,
      "org.http4s"      %% "http4s-circe"        % Http4sVersion,
      "org.http4s"      %% "http4s-dsl"          % Http4sVersion,
      "ch.qos.logback"  %  "logback-classic"     % LogbackVersion         % Runtime,
      "io.circe" %% "circe-generic" % CircleVersion,
      "io.circe" %% "circe-literal" % CircleVersion,
      "io.circe" %% "circe-parser" % CircleVersion,
      "org.tpolecat" %% "doobie-core"      % DoobieVersion,
      "org.tpolecat" %% "doobie-hikari"    % DoobieVersion,
      "org.tpolecat" %% "doobie-postgres"  % DoobieVersion,
      "org.tpolecat" %% "doobie-scalatest" % DoobieVersion % Test,
      "org.postgresql" % "postgresql" % PostgresVersionLib,
      "org.typelevel" %% "munit-cats-effect-3" % MunitCatsEffectVersion % Test,
    ),
    mainClass := Some("com.simple.site.Main")
  )

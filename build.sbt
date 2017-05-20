name := "bio-tp"
version := "1.0"
scalaVersion := "2.12.2"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

resolvers += Classpaths.typesafeSnapshots
resolvers += "Typesafe Maven Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/"
resolvers += "Typesafe Maven Releases" at "http://repo.typesafe.com/typesafe/releases/"
resolvers += "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases"
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

libraryDependencies ++= {
  val scalaTestV          = "3.0.1"
  Seq(
    "com.typesafe"                       %   "config"                     % "1.3.1",
    "org.slf4j"                          %   "slf4j-api"                  % "1.7.13",
    "ch.qos.logback"                     %   "logback-core"               % "1.1.3",
    "ch.qos.logback"                     %   "logback-classic"            % "1.1.3",
    "org.json4s"                        %%   "json4s-jackson"             % "3.5.2",
    "org.scalatest"                     %%   "scalatest"                  % scalaTestV % "test",
    "org.typelevel"                     %%   "cats"                       % "0.8.1",

    "org.biojava"                        %   "biojava-core"               % "4.2.7",
    "org.biojava"                        %   "biojava-ws"                 % "4.2.7",

    "com.github.scopt"                  %%   "scopt"                      % "3.5.0"
  )
}


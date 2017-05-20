import sbt.Keys._
import sbt.{ Package, _ }
import sbtassembly.AssemblyPlugin.autoImport.{ MergeStrategy => _, assembly => _, assemblyJarName => _, assemblyMergeStrategy => _, assemblyOption => _, _ }
import sbtassembly._


object Assembly {

  import sbtassembly.AssemblyPlugin.autoImport.PathList
  import sbtassembly.AssemblyKeys._

  lazy val settings = Seq(
    assemblyJarName in assembly := s"bio-tp.jar",
    assemblyOption in assembly := (assemblyOption in assembly).value.copy(cacheUnzip = false),
    packageOptions <+= (name, version, organization) map { (title, version, vendor) =>
      Package.ManifestAttributes(
        "Created-By" -> "Simple Build Tool",
        "Built-By" -> System.getProperty("user.name"),
        "Build-Jdk" -> System.getProperty("java.version"),
        "Specification-Title" -> title,
        "Specification-Version" -> version,
        "Specification-Vendor" -> vendor,
        "Implementation-Title" -> title,
        "Implementation-Version" -> version,
        "Implementation-Vendor-Id" -> vendor,
        "Implementation-Vendor" -> vendor,
        "Can-Redefine-Classes" -> "true",
        "Can-Set-Native-Method-Prefix" -> "true",
        "Can-Retransform-Classes" -> "true")
    },
    assemblyMergeStrategy in assembly := {
      case PathList(ps@_*) if ps.last == "application.conf" => MergeStrategy.concat
      case PathList(ps@_*) if ps.last == "asm-license.txt" => MergeStrategy.concat
      case PathList(ps@_*) if ps.last == "logback.xml" => MergeStrategy.first
      case PathList(ps@_*) if ps.last.toLowerCase endsWith ".osm" => MergeStrategy.discard
      case PathList(ps@_*) if ps.last.toLowerCase endsWith ".csv" => MergeStrategy.discard
      case x =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
    }
  ) ++ addArtifact(artifact in(Compile, assembly), assembly) ++ (test in assembly := {}) ++ assemblyArtifact

  lazy val assemblyArtifact = artifact in (Compile, assembly) := {
    val art = (artifact in (Compile, assembly)).value
    art.copy(`classifier` = Some("assembly"))
  }

  lazy val notAggregateInAssembly = Seq(aggregate in assembly := false)
  lazy val excludeScalaLib = Seq(assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false))

}

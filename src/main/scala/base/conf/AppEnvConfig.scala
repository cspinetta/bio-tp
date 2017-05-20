package base.conf

import base.LazyLoggerSupport
import com.typesafe.config.Config

trait AppEnvConfig {
  lazy val configuration = AppEnvConfig
}

object AppEnvConfig extends EnvConfig with LazyLoggerSupport {

  val source = SourceApp(envConfiguration.config.getConfig("source"))
  val output = OutputApp(envConfiguration.config.getConfig("output"))

  logger.info(s"Configuration loads OK")

}

case class SourceApp(config: Config) {
  val mRNA: String = config.getString("nRNA")
}

case class OutputApp(config: Config) {
  val fasta: String = config.getString("fasta-directory")
  val blast: String = config.getString("blast-directory")
}

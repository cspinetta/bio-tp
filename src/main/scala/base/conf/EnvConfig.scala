package base.conf

import java.io.File

import base.LazyLoggerSupport
import com.typesafe.config.{ Config, ConfigFactory }

trait EnvConfig {
  lazy val envConfiguration = EnvConfiguration
}

object EnvConfiguration extends LazyLoggerSupport {

  val config = loadConfig()

  private def loadConfig(): Config = {

    val defaultConfig = ConfigFactory.load()

    val overrideFile = new File(Option(System.getProperty("environmentOverride")).getOrElse("environment-override.conf"))
    val environment = Option(System.getProperty("environment")).getOrElse(defaultConfig.getString("environment"))
    val envConfiguration = ConfigFactory.parseFile(overrideFile).withFallback(defaultConfig.getConfig(environment))
      .withFallback(defaultConfig)

    envConfiguration.resolve()
  }

  def currentEnvironment: String = {
    Option(System.getProperty("environment")).getOrElse(config.getString("environment"))
  }

}

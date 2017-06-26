package base

import scala.sys.process.ProcessLogger

case class CmdExecutor(cmd: String) extends LazyLoggerSupport {

  def unsafeExecute: Unit = {
    import sys.process._
    val processLogger = BioProcessLogger()
    logger.info(s"Executing `$cmd`...")
    val result = cmd ! processLogger
    // drain command output
    logger.info(s"Command output: ${System.lineSeparator()}${processLogger.generateOutput}")
    if (result != 0) throw new RuntimeException(s"Command `$cmd` finished with code error: $result")
  }

}

sealed case class BioProcessLogger() extends ProcessLogger {
  private val buffer: StringBuilder = new StringBuilder()
  def out(s: => String): Unit = buffer.append(s)
  def err(s: => String): Unit = buffer.append(s)
  def buffer[T](f: => T): T = f

  def generateOutput: String = buffer.toString()
}

package bio

import base.LazyLoggerSupport
import bio.transcriptor.ProteinTranscription

object AppStarted extends App with LazyLoggerSupport {

  logger.info("Start Bio operations...")

  ProteinTranscription.transcriptFromFile()

}

package bio

import base.LazyLoggerSupport
import base.conf.AppEnvConfig
import bio.blast.BlastService
import bio.transcriptor.ProteinTranscription

object AppStarted extends App with AppEnvConfig with LazyLoggerSupport {

  logger.info("Start Bio operations...")

  private val proteinFile = ProteinTranscription.transcriptFromFile()

  BlastService.process(proteinFile, configuration.output.blast)

}

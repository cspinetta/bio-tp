package bio

import base.LazyLoggerSupport
import base.conf.AppEnvConfig
import bio.blast.BlastService
import bio.transcriptor.ProteinTranscription

import scala.language.postfixOps

case class Config(command: Command.Value = Command.NoOp,
                  inputFilePath: String = "",
                  outputFilePath: String = "")

object Command extends Enumeration {
  type Command = Value
  val Transcription, Alignment, NoOp = Value
}

object AppStarted extends App with AppEnvConfig with LazyLoggerSupport {

  logger.info("Starting Bio-TP...")

  parseParameters.map(config =>
    config.command match {
      case Command.Transcription =>
        ProteinTranscription.transcriptFromFile(config.inputFilePath, config.outputFilePath)
      case Command.Alignment =>
        BlastService.process(config.inputFilePath, config.outputFilePath)
    }
  )

  private def parseParameters: Option[Config] = {
    val parser = new scopt.OptionParser[Config]("bio-tp") {
      implicit val CommandRead: scopt.Read[Command.Value] =
        scopt.Read.reads(Command withName)

      head("bio-tp", "v1.0")

      cmd("transcription")
        .action((_, c) => c.copy(command = Command.Transcription))
        .text("Execute transcription")
        .children(
          opt[String]('i', "input")
            .action((x, c) => c.copy(inputFilePath = x))
            .text("The path to the input file")
            .required(),
          opt[String]('o', "output")
            .action((x, c) => c.copy(outputFilePath = x))
            .text("The path to the output file")
            .required()
        )

      cmd("alignment")
        .action((_, c) => c.copy(command = Command.Transcription))
        .text("Execute alignment by NCBI QBlast service")
        .children(
          opt[String]('i', "input")
            .action((x, c) => c.copy(inputFilePath = x))
            .text("The path to the input file")
            .required(),
          opt[String]('o', "output")
            .action((x, c) => c.copy(outputFilePath = x))
            .text("The path to the output file")
            .required()
        )

      checkConfig( c =>
        if (c.command == Command.NoOp) failure("You must to especify a command: transcription or alignment")
        else success )
    }
    parser.parse(args, Config())
  }
}

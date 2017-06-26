import base.LazyLoggerSupport
import base.conf.AppEnvConfig
import bio.blast.BlastService
import bio.emboss.EmbossService
import bio.transcriptor.ProteinTranscription

import scala.language.postfixOps

case class Config(command: Command.Value = Command.NoOp,
                  inputFilePath: String = "",
                  outputFilePath: String = "",
                  sequenceIndex: Option[Int] = None,
                  localService: Boolean = false,
                  dbPath: Option[String] = None,
                  embossPath: Option[String] = None)

object Command extends Enumeration {
  type Command = Value
  val Transcription, Alignment, EmbossTranslation, EmbossMotifs, NoOp = Value
}

object AppStarted extends App with AppEnvConfig with LazyLoggerSupport {

  logger.info("Starting Bio-TP...")

  parseParameters.map(config =>
    config.command match {
      case Command.Transcription =>
        ProteinTranscription.transcriptFromFile(config.inputFilePath, config.outputFilePath)
          .recover { case exc: Throwable =>
            logger.error("Failed trying to generate the protein sequence", exc) }
      case Command.Alignment if config.localService =>
        BlastService.processWithLocalBlast(config.inputFilePath, config.outputFilePath, config.dbPath.get)
          .recover { case exc: Throwable =>
            logger.error("Failed trying to generate alignment from local service", exc) }
      case Command.Alignment =>
        BlastService.processWithRemoteService(config.inputFilePath, config.outputFilePath, config.sequenceIndex.get)
          .recover { case exc: Throwable =>
            logger.error("Failed trying to generate alignment from remote service", exc) }
      case Command.EmbossTranslation =>
        EmbossService.extractProteinSequence(config.embossPath.get, config.inputFilePath, config.outputFilePath)
          .recover { case exc: Throwable =>
            logger.error("Failed trying to generate transalations via local EMBOSS (program coderest)", exc) }
      case Command.EmbossMotifs =>
        EmbossService.calculateMotifs(config.embossPath.get, config.inputFilePath, config.outputFilePath)
          .recover { case exc: Throwable =>
            logger.error("Failed trying to calculate the motifs via local EMBOSS (program patmatmotifs)", exc) }
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
        .action((_, c) => c.copy(command = Command.Alignment))
        .text("Execute alignment by NCBI QBlast service")
        .children(
          opt[String]('i', "input")
            .action((x, c) => c.copy(inputFilePath = x))
            .text("The path to the input file")
            .required(),
          opt[String]('o', "output")
            .action((x, c) => c.copy(outputFilePath = x))
            .text("The path to the output file")
            .required(),
          opt[Int]('i', "index")
            .action((x, c) => c.copy(sequenceIndex = Some(x)))
            .text("index in protein sequence (only for remote service)"),
          cmd("local")
            .action((_, c) => c.copy(localService = true))
            .text("Using local DB...")
            .children(
              opt[String]('d', "dbpath")
                .action((x, c) => c.copy(dbPath = Some(x)))
                .text("Local DB path to use with Blast")
                .required()
          )
        )

      cmd("emboss-translation")
        .action((_, c) => c.copy(command = Command.EmbossTranslation))
        .text("Generate protein translations via EMBOSS coderest")
        .children(
          opt[String]('i', "input")
            .action((x, c) => c.copy(inputFilePath = x))
            .text("The path to the input file")
            .required(),
          opt[String]('o', "output")
            .action((x, c) => c.copy(outputFilePath = x))
            .text("The path to the output file")
            .required(),
          opt[String]('e', "embossdir")
            .action((x, c) => c.copy(embossPath = Some(x)))
            .text("EMBOSS directory path")
        )

      cmd("emboss-motifs")
        .action((_, c) => c.copy(command = Command.EmbossMotifs))
        .text("Calculate motifs from protein translations via EMBOSS patmatmotifs")
        .children(
          opt[String]('i', "input")
            .action((x, c) => c.copy(inputFilePath = x))
            .text("The path to the input file")
            .required(),
          opt[String]('o', "output")
            .action((x, c) => c.copy(outputFilePath = x))
            .text("The path to the output file")
            .required(),
          opt[String]('e', "embossdir")
            .action((x, c) => c.copy(embossPath = Some(x)))
            .text("EMBOSS directory path")
        )

      checkConfig( c =>
        if (c.command == Command.NoOp) failure("You must indicate a command: transcription or alignment")
        else success )

      checkConfig( c =>
        if (c.command == Command.Alignment && c.localService && c.dbPath.isEmpty) failure("You must provide a local DB path to use with Blast tool")
        else success )

      checkConfig( c =>
        if (c.command == Command.Alignment && !c.localService && c.sequenceIndex.isEmpty) failure("You must indicate what index use from fasta file")
        else success )
    }
    parser.parse(args, Config())
  }
}

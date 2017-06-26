package bio.emboss

import java.io.File
import java.nio.file.Paths

import base.{CmdExecutor, LazyLoggerSupport, MeterSupport}

import scala.util.Try

trait EmbossService extends LazyLoggerSupport with MeterSupport {

  def extractProteinSequence(embossPath: String, gbInput: String, outputPath: String): Try[Unit] = Try {
    val coderetPath = Paths.get(embossPath.toString).resolve("coderet")
    if (!new File(coderetPath.toUri).isFile) throw new RuntimeException(s"$embossPath is not a valid directory")

    val coderetOutput = s"$outputPath.coderest"
    val translationsOutput = s"$outputPath.prot"
    val cmdExecutor = CmdExecutor(
      s"$coderetPath $gbInput -nocds -norest -nomrna -outfile $coderetOutput -translationoutseq $translationsOutput")

    cmdExecutor.unsafeExecute

    logger.info(s"Translations via EMBOSS coderest finished successfully. Result saved in $translationsOutput")
  }

  def calculateMotifs(embossPath: String, proteinInput: String, outputPath: String): Try[Unit] = Try {
    val patmatmotifsPath = Paths.get(embossPath.toString).resolve("patmatmotifs")
    if (!new File(patmatmotifsPath.toUri).isFile) throw new RuntimeException(s"$embossPath is not a valid directory")

    val cmdExecutor = CmdExecutor(
      s"$patmatmotifsPath -sequence $proteinInput -outfile $outputPath")

    cmdExecutor.unsafeExecute

    logger.info(s"Calculated motifs via EMBOSS patmatmotifs finished successfully. Result saved in $outputPath")
  }
}

object EmbossService extends EmbossService

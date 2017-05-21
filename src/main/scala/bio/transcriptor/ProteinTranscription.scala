package bio.transcriptor

import java.io.File

import base.{FileUtilsSupport, LazyLoggerSupport}
import base.conf.AppEnvConfig
import org.biojava.nbio.core.sequence.io.{FastaWriterHelper, GenbankReaderHelper}
import org.biojava.nbio.core.sequence.transcription.Frame

import scala.collection.JavaConverters._
import scala.util.Try

trait ProteinTranscription extends LazyLoggerSupport with AppEnvConfig with FileUtilsSupport {

  def transcriptFromFile(mRNAFilePath: String, fastaOutputPath: String): Try[File] = Try {

    logger.info("Translation from RNA to the corresponding peptide sequences")

    logger.info(s"Opening file with mRNA sequences: $mRNAFilePath")
    val mRNAFile = new File(mRNAFilePath)

    val dNASequences = GenbankReaderHelper.readGenbankDNASequence(mRNAFile).asScala

    val proteinSeqs = for {
      (key, dnaSeq) <- dNASequences.toList
      (frame, index) <- Frame.getAllFrames.toSeq.zipWithIndex // [(Frame.ONE, 0), (Frame.TWO, 1) ...]
    } yield {
      val rnaSeq = dnaSeq.getRNASequence(frame)
      logger.info(s"$key - Transcription mRNA to Protein with Frame $frame")
      val proteinSeq = rnaSeq.getProteinSequence()
      proteinSeq.setOriginalHeader(Integer.toString(index + 1))
      proteinSeq
    }

    val outputFile = fileUtils.getWriteableFile(fastaOutputPath)

    logger.info(s"Saving protein sequence in FASTA file: ${outputFile.getAbsolutePath}")

    FastaWriterHelper.writeProteinSequence(outputFile, proteinSeqs.asJavaCollection)

    outputFile
  }

}

object ProteinTranscription extends ProteinTranscription

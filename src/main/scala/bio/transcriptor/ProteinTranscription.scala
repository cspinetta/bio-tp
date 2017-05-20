package bio.transcriptor

import java.io.File

import base.LazyLoggerSupport
import base.conf.AppEnvConfig
import org.biojava.nbio.core.sequence.io.{FastaWriterHelper, GenbankReaderHelper}
import org.biojava.nbio.core.sequence.transcription.Frame

import scala.collection.JavaConverters._

object ProteinTranscription extends LazyLoggerSupport with AppEnvConfig {

  def transcriptFromFile(): Unit = {

    logger.info("Translation from RNA to the corresponding peptide sequences")

    logger.info(s"Opening file with mRNA sequences: ${configuration.source.mRNA}")
    val mRNAFile = new File(configuration.source.mRNA)

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

    new File(configuration.output.fasta).mkdirs()
    val outputFile = new File(configuration.output.fasta + File.separator + "ex1.fas")
    outputFile.createNewFile()

    logger.info(s"Saving protein sequence in FASTA file: ${outputFile.getAbsolutePath}")

    FastaWriterHelper.writeProteinSequence(outputFile, proteinSeqs.asJavaCollection)
  }

}

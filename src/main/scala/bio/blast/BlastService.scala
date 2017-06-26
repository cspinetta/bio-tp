package bio.blast

import java.io.{BufferedReader, File, InputStreamReader}
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import java.nio.file.{Files, Paths}

import base.{CmdExecutor, LazyLoggerSupport, MeterSupport}
import org.biojava.nbio.core.sequence.ProteinSequence
import org.biojava.nbio.core.sequence.io.FastaReaderHelper
import org.biojava.nbio.core.sequence.io.util.IOUtils
import org.biojava.nbio.ws.alignment.qblast._

import scala.collection.JavaConverters._
import scala.util.Try

trait BlastService extends LazyLoggerSupport with MeterSupport {

  def processWithRemoteService(fastaInput: String, outputPath: String, sampleIndex: Int): Try[Unit] = Try {
    val blastService = new NCBIQBlastService()
    val outputFilePath = Paths.get(outputPath)

    val fastaFile =   new File(fastaInput)

    val proteinSeq = FastaReaderHelper.readFastaProteinSequence(fastaFile)

    val withRightElORF: ProteinSequence = proteinSeq.asScala.values.toSeq.lift(sampleIndex - 1)
      .getOrElse(throw new RuntimeException(s"Sequence Index is out of bound: [1, ${proteinSeq.size()}]"))

    val alignmentProp = new NCBIQBlastAlignmentProperties()

    alignmentProp.setBlastProgram(BlastProgramEnum.blastp)
    alignmentProp.setBlastDatabase("swissprot")

    val outputProp = new NCBIQBlastOutputProperties()

    outputProp.setOutputFormat(BlastOutputFormatEnum.Text)

    var reader: Option[BufferedReader] = None
    try {

      val reqId = withTimeLoggingInSeconds({
        val reqId = blastService.sendAlignmentRequest(withRightElORF.getSequenceAsString, alignmentProp)

        while (!blastService.isReady(reqId)) {
          logger.info(s"Waiting for result of RequestID $reqId. Sleeping for 1 second.")
          Thread.sleep(1000)
        }

        reqId

      }, (time: Long) =>
        logger.info(s"Received alignment result from NCBI QBlast service in $time seconds."))

      val inputStream = blastService.getAlignmentResults(reqId, outputProp)

      reader = Some(new BufferedReader(new InputStreamReader(inputStream)))

      new File(outputFilePath.toString).getParentFile.mkdirs
      Files.copy(inputStream, outputFilePath, REPLACE_EXISTING)

      logger.info(s"Saving BLAST result in file: ${outputFilePath.toString}")

    } finally reader.foreach(IOUtils.close)

    logger.info(s"Alignments via NCBI QBlast Service finished successfully. Result saved in $outputPath")
  }

  def processWithLocalBlast(fastaInput: String, outputPath: String, dbPath: String): Try[Unit] = Try {
    val cmdExecutor = CmdExecutor(s"blastall -p blastp -d $dbPath -i $fastaInput -o $outputPath")
    cmdExecutor.unsafeExecute
    logger.info(s"Alignments via local DB finished successfully. Result saved in $outputPath")
  }

}

object BlastService extends BlastService

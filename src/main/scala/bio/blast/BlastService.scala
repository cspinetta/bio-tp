package bio.blast

import java.io.{BufferedReader, File, InputStreamReader}
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import java.nio.file.{Files, Paths}

import base.{LazyLoggerSupport, MeterSupport}
import org.biojava.nbio.core.sequence.ProteinSequence
import org.biojava.nbio.core.sequence.io.FastaReaderHelper
import org.biojava.nbio.core.sequence.io.util.IOUtils
import org.biojava.nbio.ws.alignment.qblast._

import scala.collection.JavaConverters._

trait BlastService extends LazyLoggerSupport with MeterSupport {

  def process(fastaInput: String, outputPath: String,
              outputFilename: String = "Exercise-2-blast_result.txt"): String = {
    val blastService = new NCBIQBlastService()
    val outputFilePath = Paths.get(outputPath)

    val fastaFile =   new File(fastaInput)

    val proteinSeq = FastaReaderHelper.readFastaProteinSequence(fastaFile)

    val withRightElORF: ProteinSequence = proteinSeq.asScala.values.head

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

      Files.copy(inputStream, outputFilePath, REPLACE_EXISTING)

      logger.info(s"Saving BLAST result in file: ${outputFilePath.getFileName}")

    } finally reader.foreach(IOUtils.close)

    outputFilePath.toString

  }

}

object BlastService extends BlastService

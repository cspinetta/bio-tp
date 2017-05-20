package bio.blast

import java.io.{BufferedReader, File, InputStreamReader}

import base.LazyLoggerSupport
import org.biojava.nbio.core.sequence.ProteinSequence
import org.biojava.nbio.core.sequence.io.FastaReaderHelper
import org.biojava.nbio.core.sequence.io.util.IOUtils
import org.biojava.nbio.ws.alignment.qblast._

import scala.collection.JavaConverters._

object BlastService extends LazyLoggerSupport {

  def process(fastaInput: File): Unit = {
    val blastService = new NCBIQBlastService()

    val proteinSeq = FastaReaderHelper.readFastaProteinSequence(fastaInput)

    val withRightElORF: ProteinSequence = proteinSeq.asScala.values.head

    val alignmentProp = new NCBIQBlastAlignmentProperties()

    alignmentProp.setBlastProgram(BlastProgramEnum.blastp)
    alignmentProp.setBlastDatabase("swissprot")

    val outputProp = new NCBIQBlastOutputProperties()

    outputProp.setOutputFormat(BlastOutputFormatEnum.Text)

    var reader: Option[BufferedReader] = None
    try {

      val reqId = blastService.sendAlignmentRequest(withRightElORF.getSequenceAsString, alignmentProp)

      while (!blastService.isReady(reqId)) {
        logger.info("Waiting for result. Sleeping for 1 minute.")
        Thread.sleep(1000)
      }

      val inputStream = blastService.getAlignmentResults(reqId, outputProp)

      reader = Some(new BufferedReader(new InputStreamReader(inputStream)))
    } finally reader.foreach(IOUtils.close)

  }

}

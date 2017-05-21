package base

import java.io.File

trait FileUtilsSupport {
  val fileUtils = FileUtils
}

trait FileUtils {

  def getWriteableFile(path: String): File = {
    val outputFile = new File(path)
    outputFile.getParentFile.mkdirs
    outputFile.createNewFile()
    outputFile
  }

}

object FileUtils extends FileUtils

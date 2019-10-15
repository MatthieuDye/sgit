package sgit

import java.io.File

import scala.language.postfixOps
import java.nio.file.Files

import scala.io.Source


class Addition {

}
object Addition {

  def addFiles(files: File*): Unit = {
    if (!files.isEmpty) {
      add(files.head)
      this.addFiles(files.tail: _*)
    }
  }

  def add(files: File*): Boolean = {

    if (!files.isEmpty) {
      val pathFile = files.head.getPath
      //Files.readAllBytes(files.head.toPath)

      val blob = Blob(pathFile)
       blob.addBlobToIndex(blob.createBlobFile)

    }
    true
  }
}



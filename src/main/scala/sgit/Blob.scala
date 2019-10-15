package sgit

import com.roundeights.hasher.Implicits._

import java.io.{ File, PrintWriter}
import java.nio.file.Paths

import scala.io.Source


/**
  * Class used to create Blob, and generate Blob file in .sgit/objects folder
  * @param filePath, content
  */
class Blob private (private val filePath : String) {

  /**
    * Create a blob file in your ./sgit directory and fill it with hashed content
    * @return file, blob file created
    */
  def createBlobFile : File = {
    val bufferedSource = Source.fromFile(filePath)

    val content = bufferedSource.getLines().mkString("\n")
    val shaContent = content.sha512.hex
    val blobPath = Constants.OBJECTS_PATH+shaContent
    // blob content should be crypted file content
    // blob name should be hashed file content
      // here, blob share name and content

    val blobFile = new File(blobPath)
    val writer = new PrintWriter(blobFile)
    writer.write(content.toString)
    writer.close()

    bufferedSource.close()

  blobFile
}

  def getPath : String = filePath

  def getBlobName = {
    if (filePath.contains("\\")) filePath.substring(filePath.lastIndexOf('\\'))
    else filePath
  }

  def getBlobReference = {
    Blob.BLOB_TYPE+" "+getBlobHash()+" "+getBlobName
  }

  def getBlobHash(index : File = Paths.get(Constants.INDEX_PATH).toFile) :String = {
    val bufferedSource = Source.fromFile(index)
    // TODO if don't contain ?
    val blobHash = bufferedSource.getLines().filter(line => line.split(" ").head.equals(filePath)).mkString.split(" ").last
    bufferedSource.close()
    blobHash
  }

  def addBlobToIndex(blobFile : File, index : File = Paths.get(Constants.INDEX_PATH).toFile) : Unit = {
    val bufferedSource = Source.fromFile(index)
    val content = bufferedSource.getLines().toList

    if (content.isEmpty) {
      val writer = new PrintWriter(new File(Constants.INDEX_PATH))
      writer.write(this.filePath.substring(filePath.indexOf("\\")+1)+" "+blobFile.getName+"\n")
      writer.close()
    } else {

      if (!content.contains(this.filePath)) {

        val newContent = content:+(this.filePath.substring(filePath.indexOf("\\")+1)+" "+blobFile.getName)

        val writer = new PrintWriter(new File(Constants.INDEX_PATH))
        writer.write(newContent.mkString("\n"))
        writer.close()

      } else {

        val newContent = bufferedSource.getLines().toList.filterNot(e => e.contains(this.filePath.substring(filePath.indexOf("\\")+1)))+:(this.filePath+" "+blobFile.getName+"\n")
        val writer = new PrintWriter(new File(Constants.INDEX_PATH))
        writer.write(newContent.mkString("\n"))
        writer.close()
      }
    }
    bufferedSource.close()

  }

}

object Blob {
  val BLOB_TYPE = "blob"
  def apply(filePath: String): Blob = new Blob(filePath)

  def getBlobFromPath(pathToBlob: String, index : File = Paths.get(Constants.INDEX_PATH).toFile): File = {
    val bufferedSource = Source.fromFile(index)
    val blobHash = bufferedSource.getLines().filter(line => line.splitAt(line.indexOf(" "))._1.equals(pathToBlob)).mkString
    bufferedSource.close()
    Paths.get(Constants.OBJECTS_PATH+blobHash).toFile
  }
}

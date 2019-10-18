package sgit.FileAccessObjects

import better.files.File
import com.roundeights.hasher.Implicits._
import sgit.constants.{ ConstPaths => Constants }
import sgit.utils.SGitUtils


class Blob private (private val originFilePath : String, private val name : String, private val encryptedContent : String ) {

  def getBlobOriginFile : String = originFilePath
  def getBlobName : String = name
  def getBlobEncryptedContent : String = encryptedContent
  def getHashedBlobName : String = getBlobEncryptedContent.sha256.hex
  def getBlobPath : String = Constants.OBJECTS+getHashedBlobName

  /**
    * Create a blob file in your ./sgit directory and fill it with hashed content
    * @return file, blob file created
    */
  def getBlobIndexRef : (String, String) = {
    (getBlobOriginFile, getHashedBlobName)
  }

  def getBlobReference: String = {
    Blob.BLOB_TYPE+" "+getHashedBlobName+" "+getBlobName
  }

}

object Blob {
  val BLOB_TYPE = "blob"
  def apply(file : File): Blob =
    new Blob(
      file.pathAsString.diff(System.getProperty("user.dir")+"\\"),
      file.name,
      file.contentAsString.sha256.hex // pseudo hachage
      )

  def getBlobFromPath(pathToBlob: String, index : File = File(Constants.INDEX)): File = {

    val blobHash = index.lines.filter(line => line.splitAt(line.indexOf(" "))._1.equals(pathToBlob)).mkString
    File(Constants.OBJECTS+blobHash)
  }
}

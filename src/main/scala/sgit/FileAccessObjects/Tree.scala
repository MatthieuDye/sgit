package sgit.FileAccessObjects

import com.roundeights.hasher.Implicits._

import scala.annotation.tailrec


sealed abstract class Tree {
  def getRef : String
  def getName : String
  def getType : String

  def isNode : Boolean = {
    getRef.contains("tree")
  }

  def getPath : String

  def isLeaf: Boolean = !isNode

  def getChildren : List[Tree]

  def getChildrenRefs:List[String]

  def getHashName:String

  override def toString: String
}

case class NodeFolder(filePath: String, children: Tree*) extends Tree {

  override def getType: String = NodeFolder.TREE_TYPE

  override def getName: String = filePath.substring(filePath.lastIndexOf("\\")+1)
  override def getPath : String = filePath

  override def getRef: String = {
    if (filePath.isEmpty) "tree "+getHashName else
    "tree "+getHashName+" "+getName
  }

  def getChildren : List[Tree] = children.toList

  override def getHashName : String = getChildrenRefs().mkString.sha256.hex

  @tailrec
  private def getChildrenRefs(childrenList: List[Tree] = children.toList, resultList : List[String] = List.empty[String]): List[String] = {
    if (childrenList.isEmpty) {resultList}
    else {
      getChildrenRefs(childrenList.tail, resultList:+childrenList.head.getRef)
    }
  }

  override def getChildrenRefs: List[String] = getChildrenRefs()

}
object NodeFolder {
  val TREE_TYPE = "tree"

  def apply(name: String, children: Tree*): NodeFolder = new NodeFolder(name, children:_*)

}

import better.files.File
import com.roundeights.hasher.Implicits._
import sgit.constants.{ ConstPaths => PATH }

case class Blob (private val originFilePath : String, private val name : String, private val encryptedContent : String ) extends Tree {

  def getOriginFilePath : String = originFilePath
  override def getName : String = name
  def getBlobEncryptedContent : String = encryptedContent
  override def getHashName : String = getBlobEncryptedContent.sha256.hex
  override def getPath : String = PATH.OBJECTS+getHashName

  /**
    * Create a blob file in your ./sgit directory and fill it with hashed content
    * @return file, blob file created
    */
  def getBlobIndexRef : (String, String) = {
    (originFilePath, getHashName)
  }

  override def getRef: String = {
    getType+" "+getHashName+" "+getName
  }

  override def getType: String = Blob.BLOB_TYPE

  override def getChildren: List[Tree] = Nil

  override def getChildrenRefs: List[String] = Nil
}

object Blob {
  val BLOB_TYPE = "blob"
  def apply(file : File): Blob =
    new Blob(
      file.pathAsString.toSeq.diff(System.getProperty("user.dir")+"\\").unwrap,
      file.name,
      file.contentAsString // pseudo hashing
    )
}

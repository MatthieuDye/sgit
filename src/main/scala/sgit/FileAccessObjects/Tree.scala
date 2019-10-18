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

case class Leaf(blob: Blob) extends Tree {
  override def getType: String = Blob.BLOB_TYPE

  override def getName: String = blob.getBlobName
  override def getRef: String = blob.getBlobReference

  override def getPath: String = blob.getBlobPath

  override def getHashName : String = blob.getHashedBlobName

  override def getChildrenRefs: List[String] = Nil

  override def getChildren: List[Tree] = Nil
}

object Leaf {
  def apply(blob: Blob): Leaf = new Leaf(blob)

}
package sgit

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

  def getHash:String

  override def toString: String
}

case class NodeFolder(filePath: String, children: Tree*) extends Tree {
  override def getType: String = "tree"
  override def getName: String = filePath.substring(filePath.lastIndexOf("\\")+1)

  override def getPath : String = filePath

  override def getRef: String = {
    "tree "+getHash+" "+getName
  }

  def getChildren : List[Tree] = children.toList

  override def getHash : String = getName.sha512.hex

  @tailrec
  private def getChildrenRefs(childrenList: List[Tree] = children.toList, resultList : List[String] = List.empty[String]): List[String] = {
    if (childrenList.isEmpty) {resultList}
    else {
      getChildrenRefs(childrenList.tail, resultList:+childrenList.head.getRef)
    }
  }

  override def getChildrenRefs: List[String] = getChildrenRefs()

  override def toString: String = "Node : "+getPath+", children : [ "+children.map(tree => tree.getType+" : "+tree.getPath)+" ]"
}
object NodeFolder {
  def apply(name: String, children: Tree*): NodeFolder = new NodeFolder(name, children:_*)
}

case class Leaf(blob: Blob) extends Tree {
  override def getType: String = Blob.BLOB_TYPE

  override def getName: String = blob.getBlobName
  override def getRef: String = blob.getBlobReference

  override def getPath: String = blob.getPath

  override def getHash : String = blob.getBlobHash()

  override def getChildrenRefs: List[String] = Nil

  override def getChildren: List[Tree] = Nil
}

object Leaf {
  def apply(blob: Blob): Leaf = new Leaf(blob)
}
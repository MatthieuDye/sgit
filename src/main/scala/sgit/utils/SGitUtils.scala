package sgit.utils

import better.files.File
import sgit.FileAccessObjects.{Blob, Index, Leaf, NodeFolder, Tree}
import sgit.constants.{ConstPaths => PATH}

import scala.annotation.tailrec

object SGitUtils {

  def getLastCommit : Option[String] = {
    val pathOfCurrentCommit = {
      File(PATH.HEAD).lines.filter(el => el.startsWith("ref:")).map(el => el.split(" ").tail.mkString).mkString
    }

    if (File(PATH.SGIT+pathOfCurrentCommit).exists) {
      Some(File(PATH.SGIT+pathOfCurrentCommit).contentAsString)
    } else None
  }


  def updateIndex( blob : Blob) : Unit = {
    File(PATH.INDEX).lines.map(line => if (line.startsWith(blob.getBlobOriginFile)) blob.getBlobIndexRef else line )
  }

  def getLastFolderFromPath(str: String) : String = {
    if (!str.contains("\\")) str
    else {
      str.substring(str.substring(0, str.lastIndexOf("\\")).lastIndexOf("\\"),str.lastIndexOf("\\"))
    }
  }
  def getFirstFolderFromPath(str: String) : String = {
    if (!str.contains("\\")) ""
    else {
      str.substring(0, str.indexOf("\\"))
    }
  }

  def getParentFolder(str: String) : String = {
    str.splitAt(str.lastIndexOf("\\"))._1
  }

  def getDepthOf(str: String) : Int = {
    if (str.isEmpty) 0
    else if (!str.contains("\\")) {
      1
    } else {
      str.split('\\').length
    }
  }

  def createTreeFromIndex(index: Index) : Tree = {

    val listContent = index.getMapIndex.keySet.toList

    @tailrec
    def createTreeWithTailRec(depthAcc: Int, listMap: Map[String, List[String]], acc: Tree*): List[Tree] = {
      if (depthAcc<1) {
        acc.toList
      } else {
          createTreeWithTailRec(
          depthAcc-1,
          createMapFromList(listContent, depthAcc-1),
          listMap.toList
            .map(keyValue => NodeFolder(keyValue._1,
            keyValue._2
              .map(path =>
                if (acc.exists(tree => tree.getPath.equals(path))) { acc.find(tree => tree.getPath.equals(path)).get
                } else {
                  Leaf(Blob(File(path)))
                }
              ):_*)
          ):_*
        )
          }
      }
    createTreeWithTailRec(getMaxDepthValue(listContent), createMapFromList(listContent, getMaxDepthValue(listContent)) ).last

  }

  def getMaxDepthValue(listContent: List[String], max: Int = 0) : Int = {
    if (listContent.isEmpty) max
    else {
      if (getDepthOf(listContent.head)>max) getMaxDepthValue(listContent.tail,getDepthOf(listContent.head) )
      else getMaxDepthValue(listContent.tail,max)
    }
  }

  def createMapFromList(listPaths: List[String], depth:Int = 1) : Map[String, List[String]] = {
    if (listPaths.nonEmpty && depth>1) {
      listPaths.filter(el => getDepthOf(el)>=depth).map(el => if (getDepthOf(el) > depth)getParentFolder(el) else el).distinct.groupMap(path => getParentFolder(path)
      )(list => list)
    } else {
      Map("" ->  listPaths.map(el => el.split('\\').head ).distinct)
    }

  }

  def getTreeFromCommitHash( hash: Option[String], listTree : List[Tree] = List.empty[Tree] ) : Option[Tree] = {
    if (hash.isEmpty) None
    else  {
      val rootTreeHash = File(PATH.OBJECTS_TREES+hash.get).lines.toList.head.split( " ").tail.mkString

      Some(
        NodeFolder(
          "./",
          File(PATH.OBJECTS_TREES+"/"+rootTreeHash).lines.toList
            .map(
              childTreeRef => createTreeFromHash(
                childTreeRef.split( " ").toList.apply(0),
                childTreeRef.split( " ").toList.apply(1),
                childTreeRef.split( " ").toList.apply(2)
              ).get
            ):_*
        )
      )
    }


  }

  def createTreeFromHash (treeType : String, treeHash: String, treeName: String) : Option[Tree] = {
    if (treeType.equals(NodeFolder.TREE_TYPE)) Some(
      NodeFolder(treeName, File(PATH.OBJECTS_TREES+"/"+treeHash).lines.toList
        .map(
          childTreeRef => createTreeFromHash(
            childTreeRef.split( " ").toList.apply(0),
            childTreeRef.split( " ").toList.apply(1),
            childTreeRef.split( " ").toList.apply(2)
          ).get
        ):_*)) else {getLeafFromBlob(getBlobFromHash(treeHash))}
  }

  def getLeafFromBlob(maybeBlob: Option[Blob]) : Option[Leaf] = {
    maybeBlob match {
      case Some(blob) => Some(Leaf(blob))
      case None => None
    }
  }

  def getBlobFromHash(hash: String) : Option[Blob] = {
    // get blob from file from index
    getIndex() match {
      case Left(_) => None
      case Right(index) => Some(Blob(
        File(index.getMapIndex.find(tuple => tuple._2.equals(hash)).get._1)))
    }

  }

  def isReferencedByCurrentCommitTree(pathOfElement : String) : Boolean = {
    if (getTreeFromCommitHash(getCurrentCommitHash()).isEmpty) {
      false
    } else
    treeContainsElement( Leaf(Blob(File(pathOfElement))), getTreeFromCommitHash(getCurrentCommitHash()).get)
  }

  /**
    *
    * @param head file where can get the current commit hash. Default is HEAD file
    * @return path to current commit file, to use to find it in objects/trees ref/heads/master
    */
  def getCurrentCommitHash(head : File = File(PATH.HEAD)): Option[String] = {
    val pathFromRef = head.lines.filter(line => line.startsWith("ref:")).mkString.split(" ").tail.mkString
    if (File(PATH.SGIT+pathFromRef).exists) {
      Some(File(PATH.SGIT+pathFromRef).contentAsString)
    } else None
  }

  def hasChangedFromCurrentCommitTree(pathFromIndex: String, indexHashedContent: String) : Boolean = {
    isReferencedByCurrentCommitTree(pathFromIndex) && !File(pathFromIndex).contentAsString.equals(indexHashedContent)
  }

  def treeContainsElement( searchedLeaf: Leaf, remainingTrees : Tree* ): Boolean = {
    if (remainingTrees.isEmpty) false else {
      val currentTree = remainingTrees.head
      if (currentTree.getChildren.exists(child => child.getRef.equals(searchedLeaf.getRef))) true
      else treeContainsElement(searchedLeaf, remainingTrees.tail++currentTree.getChildren:_*)
    }
  }

  def getCurrentBranch(head : File = File(PATH.HEAD)): String = {
    head.lines.filter(line => line.startsWith("ref:")).mkString.split('\\').tail.mkString
  }

  def encryptContent = ???
  def hashContent = ???

  def getIndex(index : File = File(PATH.INDEX) ): Either[String, Index] = {
    if (index.exists) {
      Right(Index(index.lines.map(line => (line.split(" ").head, line.split(" ").tail.mkString)).toMap))
    } else Left("Error : no Index file found.")
  }
}

package sgit

import java.io.{File, PrintWriter}
import java.nio.file.Paths

import com.roundeights.hasher.Implicits._

import scala.annotation.tailrec
import scala.io.Source

object SGitUtils {

  def getFileLinesContent(file : File) : List[String] = {
    val buffer = Source.fromFile(file)
    val listContent = buffer.getLines().toList
    buffer.close()
    listContent
  }

  def getListPathsFromIndex(index : File = Paths.get(Constants.INDEX_PATH).toFile) : List[String] = {
    getFileLinesContent(index)
      .map(line => line.split(" ").head)
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

  def createTreeFromListTailRec(listContent: List[String]) : Tree = {

    @tailrec
    def createTreeWithTailRec(depthAcc: Int, listMap: Map[String, List[String]], acc: Tree*): List[Tree] = {
      println("Liste des nodes "+acc.map(tree=> tree.getName))
      println(depthAcc)
      println(listMap)

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
                  Leaf(Blob(path))
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

  def getPathsFromIndex(index : File = Paths.get(Constants.INDEX_PATH).toFile):Map[String, List[String]] = {
    val bufferedSource = Source.fromFile(index)
    val listPaths = bufferedSource.getLines()
      .toList.map(line => line.split(" ").head)
      .groupMap(path => path.splitAt(path.indexOf("\\")+1)._1 )(list => list.splitAt(list.indexOf("\\")+1)._2)
    bufferedSource.close()
    listPaths
  }

  def createMapFromList(listPaths: List[String], depth:Int = 1) : Map[String, List[String]] = {
    if (listPaths.nonEmpty && depth>1) {
      listPaths.filter(el => getDepthOf(el)>=depth).map(el => if (getDepthOf(el) > depth)getParentFolder(el) else el).distinct.groupMap(path => getParentFolder(path)
      )(list => list)
    } else {
      Map("root" ->  listPaths.map(el => el.split('\\').head ).distinct)
    }

  }

  def writeTreeFrom(pathForTrees: String, trees: Tree*) : Unit = {

    if (trees.nonEmpty) {

      println("Remaining trees to write : "+trees.map(tree => tree.getName))
      val currentTree = trees.head

      println("Currently trying to write : "+trees.head.toString)

      if (currentTree.isNode) {

        val contentTree = currentTree.getChildrenRefs
        val writer = new PrintWriter(new File(pathForTrees+currentTree.getName.sha512.hex))
        writer.write(contentTree.mkString("\n"))
        writer.close()
        writeTreeFrom(pathForTrees, trees.tail++currentTree.getChildren: _*)
      } else writeTreeFrom(pathForTrees, trees.tail: _*)
    }
  }

  def createCommitFromTree(pathForCommit: String, pathOfWriting: String, tree: Tree): Unit = {
    val writer = new PrintWriter(new File(pathForCommit+tree.getRef.sha512.hex))
    writer.write(tree.getRef)
    writer.close()

    val pathOfCommit = getFileLinesContent(Paths.get(pathOfWriting).toFile).filter(el => el.startsWith("ref:")).map(el => el.split(" ").tail.mkString).mkString

    val refCommit = new PrintWriter(new File(Constants.SGIT_PATH+pathOfCommit))
    refCommit.write(tree.getRef.sha512.hex)
    refCommit.close()
  }

}

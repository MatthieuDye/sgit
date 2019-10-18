package sgit.utils

import better.files.File
import sgit.FileAccessObjects.{Blob, Commit, Index, Tree}
import sgit.constants.{ConstPaths => PATH}

object IOUtils {

  def writeBlobs(blobs : Blob*) : Option[List[Blob]] = {
    if (blobs.nonEmpty) {
      blobs.foreach(blob => File(blob.getBlobPath).createIfNotExists().append(blob.getBlobEncryptedContent))
      Some(blobs.toList)
    } else None
  }

  def writeIndex(oldIndex : Index) : Index = {
    val newContent =
      (File(PATH.INDEX)
      .lines
      .toList
        .map(line => line.splitAt(line.indexOf(" ")))
        .toMap
        ++ oldIndex.getMapIndex)
    File(PATH.INDEX) overwrite newContent.toList.map(line => line._1+" "+line._2).mkString("\n")
    Index(newContent)
  }

  def writeCommitObject(commit: Commit) : Commit = {
    File(PATH.OBJECTS_TREES+"/"+commit.getCommitName).createIfNotExists().append(commit.getCommitContent.mkString("\n"))
    commit
  }

  def updateCurrentBranchCommit(commit : Commit) : Commit = {

    val pathOfCommit = File(PATH.HEAD).lines.map(el => if(el.startsWith("ref:")) el.split(" ").tail.mkString).mkString

    File(PATH.SGIT+"/"+pathOfCommit) overwrite commit.getCommitName
    commit
  }

  def writeTreeFrom(pathForTrees: String, initialTree: Tree, remainingTrees: Tree*) : Tree = {

    if (remainingTrees.nonEmpty) {

      val currentTree = remainingTrees.head

      if (currentTree.isNode) {
        println(currentTree.getChildrenRefs)
        File(PATH.OBJECTS_TREES+"/"+currentTree.getHashName).createIfNotExists().append(currentTree.getChildrenRefs.mkString("\n"))

        writeTreeFrom(pathForTrees, initialTree, remainingTrees.tail++currentTree.getChildren: _*)

      } else writeTreeFrom(pathForTrees,initialTree, remainingTrees.tail: _*)
    }

    initialTree
  }
}

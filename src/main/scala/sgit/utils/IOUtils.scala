package sgit.utils

import better.files.File
import sgit.FileAccessObjects.{Blob, Commit, Index, Tree}
import sgit.constants.{ConstPaths => PATH}

object IOUtils {

  def writeBlobs(maybeBlobs : Seq[Blob]) : Option[Seq[Blob]] = {
    if (maybeBlobs.nonEmpty) {
      maybeBlobs.foreach(blob => {
        File(blob.getPath).createIfNotExists().append(blob.getBlobEncryptedContent)
        println("Blob "+blob.getName+" have been created.")
      })
        Some(maybeBlobs)}
      else None
    }

  def writeIndex(oldIndex : Index) : Option[Index] = {
    val newContent =
      (File(PATH.INDEX)
      .lines
      .toList
        .map(line => line.splitAt(line.indexOf(" ")))
        .toMap
        ++ oldIndex.getMapIndex)
    File(PATH.INDEX) overwrite newContent.toList.map(line => line._1+" "+line._2).mkString("\n")
    Some(Index(newContent))
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

  def writeTree(initialTree: Tree) : Tree = {

      if (initialTree.isNode) {

        File(PATH.OBJECTS_TREES+initialTree.getHashName).createIfNotExists().append(initialTree.getChildrenRefs.mkString("\n"))

        initialTree.getChildren.foreach(child => writeTree(child))
    }

    initialTree
  }

  def printToCommitFiles(toCommitFiles : Seq[File]): Unit = {
    println("Changes to be committed:")
    println("  (use \"sgit commit\" when ready)\n")
    toCommitFiles.foreach(file => println(Console.GREEN + "\tnew file:\t" + file.pathAsString.toSeq.diff(System.getProperty("user.dir")+"\\").unwrap + Console.RESET))
    println("\r")
  }


  def printToUpdateFiles(modifiedFiles: Seq[File]): Unit = {
    if (modifiedFiles.nonEmpty) {
      println("Changes not staged for the commit:")
      println("  (use \"sgit add <file>...\" to update what will be committed)\n")
      modifiedFiles.foreach(file => println(Console.YELLOW+"\tmodified:\t"+file.pathAsString.toSeq.diff(System.getProperty("user.dir")+"\\").unwrap+Console.RESET))
      println("\r")
    }
  }

  def printUnTracked(listFiles : Seq[File]): Unit = {
    if (listFiles.nonEmpty) {
      println("Untracked files:")
      println("  (use \"sgit add <file>...\" to include in what will be committed)\n")
      listFiles.foreach(file => println(Console.RED + "\t" + file.pathAsString.toSeq.diff(System.getProperty("user.dir")+"\\").unwrap + Console.RESET))
      println("\r")
    } else {
      println("No files untracked.")
    }
  }

}

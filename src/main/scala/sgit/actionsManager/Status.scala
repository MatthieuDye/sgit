package sgit.actionsManager

import java.nio.file.Files

import better.files.Dsl.dir
import better.files.File
import sgit.constants.{ ConstPaths => PATH}
import sgit.utils.SGitUtils
import com.roundeights.hasher.Implicits._
import sgit.FileAccessObjects.Index


object Status {

  def printToCommitFiles(index: Index): Unit = {
    getFilesToCommit(index) match {
      case Left(error) => println(error)
      case Right(files) => if (files.nonEmpty) {
        println("Changes to be committed:")
        println("  (use \"sgit commit\" when ready)\n")
        files.foreach(file => println(Console.GREEN + "\tnew file:\t" + file.canonicalPath + Console.RESET))
        println("\r")
      }
    }
  }

  def printToUpdateFiles(index: Index): Unit = {
    getFilesToUpdate(index) match  {
      case  Left(error) => println(error)
      case Right(files) => if (files.nonEmpty) {
        println("Changes not staged for the commit:")
        println("  (use \"sgit add <file>...\" to update what will be committed)\n")
        files.foreach(file => println(Console.YELLOW+"\tmodified:\t"+file+Console.RESET))
        println("\r")
      }
    }
  }

  def printUntracked(index: Index): Unit = {
    getFilesToTrack(index) match {
      case Left(error) => println(error)
      case Right(files) => if (files.nonEmpty)
        println("Untracked files:")
        println("  (use \"sgit add <file>...\" to include in what will be committed)\n")
        files.foreach(file => println(Console.RED + "\t" + (if (file.isRegularFile) file.name else file.name + "/") + Console.RESET))
        println("\r")
      }
  }


  /**
    * Method used to list to-commit files, files in index not referenced in a tree
    * added (in index) but not committed
    *
    * @param index the new content of index
    * @return
    */
  def getFilesToCommit(index :Index) : Either[String, List[File]] = {
    //val treeFromCommit = SGitUtils.getTreeFromCommit(SGitUtils.getCurrentCommit())
    if (index.getMapIndex.nonEmpty) {
      Right(
        index
          .getMapIndex
          .filter(
            el =>
              !SGitUtils.isReferencedByCurrentCommitTree(el._1))
          .toList
          .map(el => File(el._1)).filter(el => !getFilesToUpdate(index).contains(el)))
    } else Left("Index is empty")
  }

  /**
    * Get still-not-added files from index which are referenced in a blob, but where true file has a different hash
    * @param index the current index
    * @return list of elements to update (means to add again)
    */
  def getFilesToUpdate(index : Index) : Either[String, List[File]] = {
    if (index.getMapIndex.nonEmpty) {
      Right(
        index
          .getMapIndex
          .filter(
           tuple =>
            !File(tuple._1).contentAsString.sha256.hex.equals(File(PATH.OBJECTS+"/"+tuple._2).contentAsString)
        )
        .toList.map(tuple => File(tuple._1)))
    } else Left("Error : Index is empty")


  }

  /**
    * Should return a list of elements which are in current directory and not tracked in index content
    * @param index, new Index
    * @return
    */
  def getFilesToTrack(index : Index): Either[String, List[File]] = {
    if (index.getMapIndex.nonEmpty) {
      Right(dir(File("./"))
        .filter(
          file => !file.name.equals(".git") && !file.name.equals(".sgit"))
        .filter(
          file => !isTracked(file, index.getMapIndex.keySet.toList))
        .toList)
    } else Left("Empty Index")
  }

  def isTracked(file: File, indexContent: List[String]) : Boolean = {
    if (file.isRegularFile && indexContent.exists(el => el.equals(file.name)) ) true
    else if (file.isDirectory) dir(file).forall(child => isTracked(child, indexContent))
    else false
  }


  def printStatus(index: Index): Unit = {
    println("On branch "+SGitUtils.getCurrentBranch())
    if(Status.getFilesToUpdate(index).isRight || Status.getFilesToCommit(index).isRight || Status.getFilesToTrack(index).isRight ) {
      Status.printToCommitFiles(index)
      //to commit files = files in index not referenced in a tree
      //added (in index) but not committed
      Status.printToUpdateFiles(index)
      // name in index, but content changed from previous commit
      Status.printUntracked(index)
      // not in index
      println("no changes added to commit (use \"sgit add\")")

    } else {
      println("nothing to commit, working tree clean")
    }


  }
}

package sgit.actionsManager

import better.files.Dsl.dir
import better.files.File
import sgit.constants.{ConstPaths => PATH}
import sgit.utils.SGitUtils
import com.roundeights.hasher.Implicits._
import sgit.FileAccessObjects.Index


object Status {

  def getFilesToAdd(index: Either[String, Index]) : Either[String, List[File]] = {
    index match {
      case Left(error:String) => Left(error)
      case Right(index: Index) => Right(
        index
          .getMapIndex
          .filter(
            tuple =>
              File(tuple._1).contentAsString.sha256.hex.equals(File(PATH.OBJECTS+"/"+tuple._2).contentAsString)
          )
          .toList.map(tuple => File(tuple._1)))
    }
  }

  /**
    * Method used to list to-commit files, files in index not referenced in a tree
    * added (in index) but not committed
    *
    * @param index the new content of index
    * @return
    */
  def getFilesToCommit(index :Either[String, Index]) : Either[String, List[File]] = {
    index match {
      case Left(error: String) => Left(error)
      case Right(index:Index) =>
        Right(
          index
            .getMapIndex
            .filter(
              el =>
                !SGitUtils.isReferencedByCurrentCommitTree(el._1))
            .toList.map(tuple => File(tuple._1)))
    }
  }


  /**
    * Get still-not-added files from index which are referenced in a blob, but where true file has a different hash
    * @param index the current index
    * @return list of elements to update (means to add again)
    */
  def getModifiedFiles(index : Either[String,Index]) : Either[String, List[File]] = {
    index match {
      case Left(error:String) => Left(error)
      case Right(index:Index) =>Right(
        index
          .getMapIndex
          .filter(
            tuple =>
              ! File(tuple._1).contentAsString.equals(File(PATH.OBJECTS+tuple._2).contentAsString)
          )
          .toList.map(tuple => File(tuple._1)))
    }
  }

  /**
    * Should return a list of elements which are in current directory and not tracked in index content
    * @param index, new Index
    * @return
    */
  def getFilesToTrack(index : Either[String, Index], filesToCheck : Seq[File]): Either[String, Seq[File]] = {
    index match {
      case Left(error) => Left(error)
      case Right(index:Index) => if (filesToCheck.isEmpty) Right(Seq.empty[File])
      else
      if (index.getMapIndex.isEmpty) Right(filesToCheck)
      else {
        Right(
          getAllFilesFrom(filesToCheck)
          .filter(
            file => !isTracked(file, index.getMapIndex.keySet.toList))
          .toList)
      }
    }


  }
  def getAllFilesFrom(value: Seq[File], filesSeq: Seq[File] =Seq.empty) : Seq[File] = {
    if (value.isEmpty) {
      filesSeq }
    else {
      if (value.head.isRegularFile) getAllFilesFrom(value.tail, filesSeq:+value.head)
      else getAllFilesFrom(dir(value.head).toSeq++value.tail, filesSeq)
    }
  }

  def isTracked(file: File, indexContent: List[String]) : Boolean = {
    if (file.isRegularFile && indexContent.exists(el => el.equals(file.pathAsString.diff(System.getProperty("user.dir")+"\\"))) ) true
    else if (file.isDirectory) dir(file).forall(child => isTracked(child, indexContent))
    else false
  }

}

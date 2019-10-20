package sgit.FileAccessObjects

import java.text.SimpleDateFormat
import java.util.Calendar
import com.roundeights.hasher.Implicits._


class Commit(initTree: Tree, parent : Option[Commit], commitDate: String, lastCommitMessage : String) {

  def getInitialTree : Tree = initTree
  def getParentCommit : String = if (parent.nonEmpty) "parent "+parent.get.getCommitName else ""
  def getCommitDate : String = commitDate
  def getCommitMessage : String = lastCommitMessage

  def getCommitContent : List[String] = "tree "+initTree.getHashName :: getParentCommit :: "author "+System.getProperty("user.name") :: commitDate :: lastCommitMessage :: Nil

  def getCommitName : String = getCommitContent.mkString.sha256.hex

}

object Commit {
  val formatter = new SimpleDateFormat("dd_MM_yyyy_hh_mm_ss")

  def apply(currentTree: Tree, parent: Option[Commit], lastCommitMessage: String = "commit", commitDate: String = formatter.format(Calendar.getInstance().getTime) ): Commit
  = new Commit(currentTree, parent, commitDate, lastCommitMessage)
}
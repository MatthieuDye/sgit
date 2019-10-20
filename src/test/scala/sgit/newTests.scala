package sgit

import better.files._
import better.files.Dsl._
import org.scalatest._
import sgit.FileAccessObjects._
import sgit.actionsManager._
import sgit.constants.{ConstPaths => Constants}
import sgit.utils.SGitUtils

class newTests extends FunSpec with Matchers with GivenWhenThen with BeforeAndAfter {

  if (File(Constants.SGIT).exists) {
    println("Deleting sgit previous folder")
    del(File(Constants.SGIT))}

  File("./1.txt").overwrite("1")

  Creation.initSGitRepo()

  describe("I initialize my repo and add some files") {

    describe("Without adding any of pre-existing files, my status displays :") {
      it("Current index contains no entries") {
        File(Constants.INDEX).lines.size shouldBe 0
      }

      it("I don't have unstaged files") {
        Status.getModifiedFiles(SGitUtils.getIndex()).getOrElse(List.empty).size shouldBe 0
      }

      it("I have no elements to commit because my index is empty") {
        Status.getFilesToCommit(SGitUtils.getIndex()).getOrElse(List.empty).size shouldBe 0
      }

      it("4 elements are considered as untracked files.") {
        Status.getFilesToTrack(SGitUtils.getIndex(), dir(File(".")).toSeq) shouldBe Right(List("C:\\rattrapages\\sgit\\tests\\.sgit", "C:\\rattrapages\\sgit\\tests\\1.txt", "C:\\rattrapages\\sgit\\tests\\celia", "C:\\rattrapages\\sgit\\tests\\matthieu", "C:\\rattrapages\\sgit\\tests\\readme.rd"))
      }
    }

    describe("I added them without one (./matthieu/5.txt)") {

      it("My file 1.txt contains 1") {
        Addition.updateIndex( SGitUtils.getIndex().getOrElse(Index(Map.empty)), Seq(Blob(File("./matthieu/dye/1.txt"))))
        Addition.updateIndex( SGitUtils.getIndex().getOrElse(Index(Map.empty)), Seq(Blob(File("./1.txt"))))
        File("./1.txt").contentAsString shouldBe "1"
      }
      it("Current index contains 8 entries and contains each added file") {
        File(Constants.INDEX).lines.size shouldBe 8
      }
      it("Current index contains some just-added elements") {
        File(Constants.INDEX).lines.toList contains "matthieu\\dye\\1.txt"
      }
      it("I don't have unstaged files") {
        Status.getModifiedFiles(SGitUtils.getIndex()) shouldBe List.empty[String]
      }
      it("I have 8 elements to commit because I just added them") {
        Status.getFilesToCommit(SGitUtils.getIndex()) shouldBe 8
      }
      it("1 is considered as untracked files.") {
        Status.getFilesToTrack(SGitUtils.getIndex(), dir(File(".")).toSeq) shouldBe Right(List("C:\\rattrapages\\sgit\\tests\\.sgit", "C:\\rattrapages\\sgit\\tests\\1.txt", "C:\\rattrapages\\sgit\\tests\\celia", "C:\\rattrapages\\sgit\\tests\\matthieu", "C:\\rattrapages\\sgit\\tests\\readme.rd"))
      }

      describe("I will modify a tracked file") {

        it("I put it 2 in place of 1 in 1.txt") {
          File("1.txt").overwrite("2").contentAsString shouldBe "2"
        }
        it("and I should have a file to update") {
          Status.getModifiedFiles(SGitUtils.getIndex()) shouldBe Right(List("./matthieu/dye/5.txt"))
        }
      }

    }


  }
}

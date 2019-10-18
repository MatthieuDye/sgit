package sgit

import better.files._
import better.files.Dsl._
import org.scalatest._
import sgit.FileAccessObjects._
import sgit.actionsManager._
import sgit.constants.{ConstPaths => Constants}

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
        Status.getFilesToUpdate().get shouldBe List.empty[String]
      }

      it("I have no elements to commit because my index is empty") {
        Status.getFilesToCommit().get.size shouldBe 0
      }

      it("4 elements are considered as untracked files.") {
        Status.getFilesToTrack().get.size shouldBe 4
      }
    }

    describe("I added them without one (./matthieu/5.txt)") {

      it("My file 1.txt contains 1") {
        Addition.add(
          Index.getIndex.get,
          File("./matthieu/dye/1.txt"),
          File("./matthieu/dye/2.txt"),
          File("./matthieu/5.txt"),
          File("./celia/dye/1.txt"),
          File("./celia/dye/2.txt"),
          File("./celia/terrol/3.txt"),
          File("./celia/terrol/4.txt"))
        Addition.add(Index.getIndex.get, File("./1.txt"))
        File("./1.txt").contentAsString shouldBe "1"
      }
      it("Current index contains 8 entries and contains each added file") {
        File(Constants.INDEX).lines.size shouldBe 8
      }
      it("Current index contains some just-added elements") {
        File(Constants.INDEX).lines.toList contains "matthieu\\dye\\1.txt"
      }
      it("I don't have unstaged files") {
        Status.getFilesToUpdate().get shouldBe List.empty[String]
      }
      it("I have 8 elements to commit because I just added them") {
        Status.getFilesToCommit().get.size shouldBe 8
      }
      it("1 is considered as untracked files.") {
        Status.getFilesToTrack().get.size shouldBe 1
      }

      describe("I will modify a tracked file") {

        it("I put it 2 in place of 1 in 1.txt") {
          File("1.txt").overwrite("2").contentAsString shouldBe "2"
        }
        it("and I should have a file to update") {
          Status.getFilesToUpdate().get.size shouldBe 1
        }
      }

    }


  }
}

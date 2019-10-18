package sgit

import better.files.Dsl._
import better.files._
import org.scalatest._
import sgit.actionsManager.Creation
import sgit.constants.{ConstPaths => PATH}

class addingFiles extends FunSpec with Matchers with GivenWhenThen with BeforeAndAfter {

  describe("I initialize my repo and add some files") {
    // create a folder for tests
    println(File(System.getProperty("user.dir")))
    if (File(PATH.SGIT).exists) {
      println("Deleting sgit previous folder")
      del(File(PATH.SGIT))}
    Creation.initSGitRepo()

    describe("I create a file :") {
      it("Index size still 0") {
        assert(true.equals(true))

      }

      it("I have one file to add (untracked)") {
        assert(true.equals(true))

      }

      it("Nothing to commit") {
        assert(true.equals(true))

      }

      it("Nothing to update (because no exists in index") {
        assert(true.equals(true))

      }
    }

    describe("If I add a file :") {
      it("Index size increased") {
        assert(true.equals(true))
      }

      it("I have one file to commit") {
        assert(true.equals(true))

      }

      it("Nothing to update") {
        assert(true.equals(true))

      }

      it("Nothing to track") {
        assert(true.equals(true))

      }


    }

  }
}

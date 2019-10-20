package sgit.usesCasesTests

import better.files.File
import org.scalatest.{BeforeAndAfter, FunSpec, GivenWhenThen, Matchers}
import sgit.constants.{ConstPaths => PATH}

class InitTest extends FunSpec with Matchers with GivenWhenThen with BeforeAndAfter  {
  //TESTS FOR THE INIT
  describe("When I run the init command") {

    it("Should create a file HEAD") {
      File(PATH.HEAD).exists() shouldBe true
    }
    it("HEAD Should contains  /master") {
      File(PATH.HEAD).contentAsString shouldBe "ref: ref/eads/master"
    }

    it("Should create a folder .sgit") {
      File(PATH.SGIT).exists() shouldBe true
    }

    it("Should create a folder .sgit/objects") {
      File(PATH.OBJECTS).exists() shouldBe true
    }

    it("Should create a folder .sgit/trees") {
      File(PATH.OBJECTS_TREES).exists() shouldBe true
    }

    it("Should create a folder .sgit/tags") {
      File(PATH.REFS_TAGS).exists() shouldBe true
    }
  }
}

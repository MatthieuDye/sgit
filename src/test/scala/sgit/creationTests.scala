package sgit

import org.scalatest._

class creationTests extends FunSpec with Matchers with GivenWhenThen with BeforeAndAfter {

  describe("First description") {
    describe("Second description") {
      it("Test assert") {
        Creation.initSgitRepo()
        assert(1 === 1)
      }
    }
  }
}

package sgit.usesCasesTests

import better.files.File
import org.scalatest.{BeforeAndAfter, FunSpec, GivenWhenThen, Matchers, path}
import sgit.FileAccessObjects.{Blob, Index}
import sgit.utils.{IOUtils, SGitUtils}

class AdditionTest extends FunSpec with Matchers with GivenWhenThen with BeforeAndAfter {

  describe("Given a 1.txt to add") {
    describe("Run the add mecanism") {

      it("Should create the corresponding blob which same content as corresponding file") {
        File("1.txt").contentAsString.equals(Blob(File("1.txt")).getBlobEncryptedContent) shouldBe true
      }

      it("Because I didn't add this file, it is not in the current index") {
        SGitUtils.getIndex() match {
          case Left(value: String) => value shouldBe empty
          case Right(index: Index) => !index.getMapIndex.contains(Blob(File("1.txt")).getHashName) shouldBe true

        }
      }

      describe("Run the function add for 1.txt") {
        it("Trying to create a blob file from a non existing file should return an error") {
          // verified at the beginning
          Seq(File("1.txt"), File("unknow")).filter(file => file.exists).map(file => Blob(file)).size shouldBe 1

          }
        it("Should add to an new entry to old: blob ref") {
          SGitUtils.getIndex() match {
            case Left(error: String) => false
            case Right(oldIndex:Index) => oldIndex.addBlobToIndex(Seq(Blob(File("1.txt")))).getMapIndex.contains(Blob(File("1.txt")).getName) &&
              oldIndex.addBlobToIndex(Seq(Blob(File("1.txt")))).getMapIndex(Blob(File("1.txt")).getName).equals(Blob(File("1.txt")).getHashName) shouldBe true
          }
        }

        describe("If I modify my 1.txt for file") {
          it("Should create a new blob") {
            val oldBlob = Blob(File("1.txt"))
            File("1.txt").append("2")
            Blob(File("1.txt")).equals(oldBlob) shouldBe false
          }

          it("Should update the index entry") {
            SGitUtils.getIndex() match {
              case Left(error: String) => false
              case Right(oldIndex:Index) => oldIndex.addBlobToIndex(Seq(Blob(File("1.txt")))).getMapIndex.contains(Blob(File("1.txt")).getName) &&
                oldIndex.addBlobToIndex(Seq(Blob(File("1.txt")))).getMapIndex(Blob(File("1.txt")).getName).equals(Blob(File("1.txt")).getHashName) shouldBe true
            }
          }

          describe("Run the function add for matthieu/dye/1.txt and matthieu/dye/2.txt:  ie a array of folder") {
            it("Should write two line in the INDEX"){

            }
            it("Should create two blob"){

            }

          }
        }
      }
    }
  }
}

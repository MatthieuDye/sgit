package sgit.actionsManager

import better.files.File
import sgit.FileAccessObjects.{Blob, Index}
import sgit.utils.{IOUtils, SGitUtils}

import scala.language.postfixOps


class Addition {

}
object Addition {

  /**
    * Manage "add <fileName>..." and "add <fileNames>...". Add one or several files
    * @param files
    */
  def add(index : Index, files: File*): Index = {

    val createdBlobs = files.map(file => Blob(file))

    IOUtils.writeBlobs(createdBlobs:_*)

    if (files.nonEmpty) {
      IOUtils.writeIndex( index.addBlobToIndex( Some(createdBlobs.toList) ) )
    } else index

  }
}



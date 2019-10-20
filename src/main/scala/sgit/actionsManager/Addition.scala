package sgit.actionsManager
import sgit.FileAccessObjects.{Blob, Index}

import scala.language.postfixOps


class Addition {

}
object Addition {

  /**
    *
    * @param index
    * @param blobs
    * @return
    */
  def updateIndex(index : Index, blobs: Seq[Blob]): Option[Index] = {
   if (index.addBlobToIndex(blobs).equals(index)) {
      None
   } else Some(index.addBlobToIndex(blobs))
  }
}



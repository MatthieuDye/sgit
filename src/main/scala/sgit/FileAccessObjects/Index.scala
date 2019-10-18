package sgit.FileAccessObjects

class Index(mapIndex : Map[String, String]) {

  def addBlobToIndex(blobs: Option[List[Blob]]): Index = if (blobs.isEmpty) this else Index(mapIndex++blobs.get.map(blob=>blob.getBlobIndexRef))

  def getMapIndex:Map[String, String] = mapIndex
}

object Index {
  def apply(mapIndex: Map[String, String]): Index = new Index(mapIndex)


}

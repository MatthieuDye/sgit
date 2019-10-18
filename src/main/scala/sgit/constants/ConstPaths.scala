package sgit.constants

object ConstPaths{
  // sgit folder
  val SGIT ="./.sgit/"

  //files
  val INDEX : String= SGIT +"STAGING_FILES"
  val HEAD : String = SGIT+"HEAD"

  // folders
  val OBJECTS : String = SGIT+"objects/"
  val OBJECTS_TREES : String = OBJECTS+"trees/"



  val REFS : String = SGIT+"refs/"
  val REFS_HEADS: String= REFS+"heads/"
  val REFS_TAGS: String = REFS+"tags/"


}

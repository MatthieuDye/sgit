package sgit

object Constants {
  // sgit folder
  val SGIT_PATH = "./.sgit/"

  //files
  val INDEX_PATH = SGIT_PATH +"STAGING_FILES"
  val HEAD_PATH = SGIT_PATH+"HEAD"

  // folders
  val OBJECTS_PATH = SGIT_PATH+"objects/"

  val TREES_PATH = OBJECTS_PATH+"trees/"

  val REF_PATH = SGIT_PATH+"refs/"
  val HEADS_REF_PATH = REF_PATH+"heads/"
  val TAGS_REF_PATH = REF_PATH+"tag/"


}
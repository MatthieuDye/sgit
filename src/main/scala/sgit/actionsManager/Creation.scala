package sgit.actionsManager

import better.files.File
import sgit.constants.{ConstPaths => Constants }

object Creation {

  def initSGitRepo(): Unit = {

    // main file creation
    if(!File(Constants.SGIT).exists()) {
      File(Constants.SGIT).createDirectory()
      File(Constants.SGIT)("dos:hidden")  =true
    }

    // folders creation
    if(!File(Constants.OBJECTS).exists()) {
      File(Constants.OBJECTS).createDirectory()
    }

    if(!File(Constants.OBJECTS_TREES).exists()) {
      File(Constants.OBJECTS_TREES).createDirectory()
    }

    if(!File(Constants.REFS).exists()) {
      File(Constants.REFS).createDirectory()
      File(Constants.REFS_HEADS).createDirectory()
      File(Constants.REFS_TAGS).createDirectory()


    }


    // files creation
    if(!File(Constants.INDEX).exists()) {
      File(Constants.INDEX).createFile()
    }

    if(!File(Constants.HEAD).exists()) {
      File(Constants.HEAD).createFile().write("ref: refs/heads/master")
    }

  }
}

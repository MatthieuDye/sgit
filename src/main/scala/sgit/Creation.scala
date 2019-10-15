package sgit

import java.io.{File, IOException, PrintWriter}
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.Paths

import org.apache.commons.lang3.SystemUtils

object Creation {

  def initSgitRepo() = {

    // main file creation
    if(!new File(Constants.SGIT_PATH).exists()) {
      new File(Constants.SGIT_PATH).mkdir()
    }

    // Hidding file
    if (SystemUtils.IS_OS_WINDOWS) {
      Files.setAttribute(Paths.get(Constants.SGIT_PATH), "dos:hidden", true, LinkOption.NOFOLLOW_LINKS)
    }

    // folders creation
    if(!new File(Constants.OBJECTS_PATH).exists()) {
      new File(Constants.OBJECTS_PATH).mkdir()
    }

    if(!new File(Constants.TREES_PATH).exists()) {
      new File(Constants.TREES_PATH).mkdir()
    }

    if(!new File(Constants.REF_PATH).exists()) {
      new File(Constants.REF_PATH).mkdir()
      new File(Constants.HEADS_REF_PATH).mkdir()
      new File(Constants.TAGS_REF_PATH).mkdir()


    }


    // files creation
    if(!new File(Constants.INDEX_PATH).exists()) {
      val writer = new PrintWriter(new File(Constants.INDEX_PATH))
      writer.write("")
      writer.close()
    }

    if(!new File(Constants.HEAD_PATH).exists()) {
      val writer2 = new PrintWriter(new File(Constants.HEAD_PATH))
      writer2.write("ref: refs/heads/master")
      writer2.close()
    }

    println("Initialized empty SGit repository in "+Paths.get(Constants.SGIT_PATH).toAbsolutePath.toString)

  }
}

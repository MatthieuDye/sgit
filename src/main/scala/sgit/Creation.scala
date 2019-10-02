package sgit

import java.io.{File, IOException, PrintWriter}
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.Path
import java.nio.file.Paths
import java.lang.System

import org.apache.commons.lang3.SystemUtils


class Creation {

}

object Creation {


  def initSgitRepo() = {

    println("Creating a local hidden sgit directory ")

    new File("./.git").mkdir()

    if (SystemUtils.IS_OS_WINDOWS) {
      val path = Paths.get("./.git")
      Files.setAttribute(path, "dos:hidden", true, LinkOption.NOFOLLOW_LINKS)
    }

    val writer = new PrintWriter(new File("./.git/index"))

    writer.write("")
    writer.close()


  }
}

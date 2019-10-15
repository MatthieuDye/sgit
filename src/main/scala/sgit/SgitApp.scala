package sgit

import java.io.File
import java.nio.file.Paths

import scala.annotation.tailrec
import scala.reflect.io.Directory

object SgitApp extends App {

  @tailrec
  def mainLoop() {
    println("Command to execute >")
    val command = scala.io.StdIn.readLine()

    command match {
      case Commands.INIT_COMMAND =>
        Creation.initSgitRepo()
        mainLoop()
      case Commands.ADD_COMMAND => // add options match
        Addition.add(Paths.get("./readme.rd").toFile)
        mainLoop()
      case Commands.COMMIT_COMMAND =>
        val root = SGitUtils.createTreeFromListTailRec(SGitUtils.getListPathsFromIndex())
        SGitUtils.writeTreeFrom(Constants.TREES_PATH, root )
        SGitUtils.createCommitFromTree(Constants.TREES_PATH, Constants.HEAD_PATH, root)
        mainLoop()
      case Commands.QUIT_COMMAND => println("Quitting the app...")
      case "reset" =>
        val directory = new Directory(new File(Constants.SGIT_PATH))
        if (directory.exists) {directory.deleteRecursively()}
        Creation.initSgitRepo()
        Addition.add(Paths.get("./readme.rd").toFile)
        Addition.add(Paths.get("./matthieu/dye/1.txt").toFile)
        Addition.add(Paths.get("./matthieu/dye/2.txt").toFile)
        Addition.add(Paths.get("./matthieu/5.txt").toFile)
        Addition.add(Paths.get("./celia/dye/1.txt").toFile)
        Addition.add(Paths.get("./celia/dye/2.txt").toFile)
        Addition.add(Paths.get("./celia/terrol/3.txt").toFile)
        Addition.add(Paths.get("./celia/terrol/4.txt").toFile)
        Addition.add(Paths.get("./1.txt").toFile)
        val root = SGitUtils.createTreeFromListTailRec(SGitUtils.getListPathsFromIndex())
        SGitUtils.writeTreeFrom(Constants.TREES_PATH, root )
        SGitUtils.createCommitFromTree(Constants.TREES_PATH, Constants.HEAD_PATH, root)
      case _ =>
        println("Unknow command")
        mainLoop()
    }
  }

  mainLoop()

}


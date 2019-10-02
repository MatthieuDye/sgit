package sgit

import scala.annotation.tailrec

object SgitApp extends App {

  @tailrec
  def mainLoop() {
    println("Command to execute >")
    val command = scala.io.StdIn.readLine()

    command match {
      case Commands.INIT_COMMAND =>
        println("Well. I will initialize your sgit repository !")
        Creation.initSgitRepo()
        mainLoop()
      case Commands.QUIT_COMMAND => println("Quitting the app...")
      case _ => mainLoop()
    }
  }

  mainLoop()

}


package sgit.commandLineParser
import sgit.actionsManager._
import java.io.{File => JFile}

import better.files.File
import scopt.OParser
import sgit.FileAccessObjects._
import sgit.constants.{ConstPaths => PATH}
import sgit.utils.{IOUtils, SGitUtils}

import scala.util.matching.Regex

case class Config(
                   foo: Int = -1,
                   out: JFile = new JFile("."),
                   xyz: Boolean = false,
                   libName: String = "",
                   maxCount: Int = -1,
                   verbose: Boolean = false,
                   debug: Boolean = false,
                   mode: String = "",
                   files: Seq[JFile] = Seq(),
                   addFiles : Seq[File] = Seq(),
                   keepalive: Boolean = false,
                   jars: Seq[JFile] = Seq(),
                   kwargs: Map[String, String] = Map(),
                   index: Either[String, Index] = SGitUtils.getIndex(File(PATH.INDEX)),
                   regex: Regex = ".".r
                 )


object Parseur extends App {

  val builder = OParser.builder[Config]
  val parser1 = {
    import builder._
    OParser.sequence(
      programName("SGit"),
      head("SGit", "1.0"),

      cmd("init")
        .action((_, c) => c.copy(mode = "init"))
        .text("Create an empty SGit repository or reinitialize an existing one."),

      cmd("status")
        .action((_, c) => c.copy(mode = "status"))
        .text("Show the working tree status"),

      cmd("log")
        .text("Show all commits starting with newest")
          .children(
            opt[String]('p', "overtime")
              .text("Show changes overtime")
              .action((x,c) => c.copy(libName = x)),
            opt[String]('s', "stat")
              .text("Show stats about changes overtime")
          ),

      cmd(name = "add")
        .action((_, c) => c.copy(mode = "add"))
        .text("Add file content to the index")
        .children(
          arg[JFile]("<file>...")
          .required()
          .unbounded()
          .action((x, c) => c.copy(files = c.files :+ x))
          .text("One or several files to add to the index")
        )
        .children(
          arg[String](".")
            .unbounded()
            .optional()
            .action((_, c) => c.index match {
              case Left(_) => c
              case Right(_) => c
            })
            .text("All files to add to index (untracked and modified)"),
        )
      ,

      cmd(name = "log"),

      cmd(name = "checkout"),

      cmd(name = "branch"),

      cmd(name = "commit")
        .action((_, c) => c.copy(mode = "commit"))
        .text("commit files"),

      cmd(name = "reset")
        .action((_, c) => c.copy(mode = "reset"))
        .text("Delete current sgit repository"),


    )
  }

  // OParser.parse returns Option[sgit.Config]
  OParser.parse(parser1, args, Config()) match {
    case Some(config) =>
      if (!File(PATH.SGIT).exists && !config.mode.equals("init")) {
        println(Console.RED+"fatal: not a sgit repository (or any of the parent directories): .sgit"+Console.RESET)
      } else {
        config.mode match {
          case "init" =>
            Creation.initSGitRepo()
            println(Console.GREEN+"Initialized empty SGit repository in "+File(PATH.SGIT).pathAsString+Console.RESET)

          case "add" =>
            config.index match {
              case Left(error) => println(error)
              case Right(index) => Addition.add(index, config.files.map(file => File(file.getAbsolutePath)):_*)

            }

          case "status" =>
            config.index match {
              case Left(error) => println(error)
              case Right(_) => Status.printStatus _
            }

          case "checkout" =>
            println(Console.YELLOW+"WORK IN PROGRESS"+Console.RESET)
            // With commit hash
              // Get commit from hash => use log / return tree

              // Write new files in directory from tree

             // Fill index with tree

              // HEAD to commit hash, NOT REF

            // With branch name

              // get hash from branch

              // write files from graph

              //fill index

            // HEAD ref to branch name

          case "branch" =>
            println(Console.YELLOW+"WORK IN PROGRESS"+Console.RESET)
            // create a new branch with given name "test"
              // create new file in .sgit/refs/heads/test
              // fill this file with current HEAD ref content

          case "commit" =>
            config.index match {
              case Left(error) => println(error)
              case Right(index) =>
                val newCommit = Commit(IOUtils.writeTreeFrom(PATH.OBJECTS_TREES, SGitUtils.createTreeFromIndex(index)), SGitUtils.getLastCommit, "new Commit")
                IOUtils.writeCommitObject(newCommit)
                IOUtils.updateCurrentBranchCommit(newCommit)

            }
          case "reset" => if (File(PATH.SGIT).exists) File(PATH.SGIT).delete()

          case _ =>
            println(Console.YELLOW+"Internal error : not valid arguments"+Console.RESET)
        }
      }

    case None => println(Console.BLUE+"Internal Error : not a valid command"+Console.RESET)
  }
}
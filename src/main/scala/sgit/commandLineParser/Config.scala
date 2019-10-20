package sgit.commandLineParser
import sgit.actionsManager._
import java.io.{File => JFile}

import better.files._
import better.files.Dsl._   // must import Dsl._ to bring in these utils

import scopt.OParser
import sgit.FileAccessObjects._
import sgit.constants.{ConstPaths => PATH}
import sgit.utils.{IOUtils, SGitUtils}

import scala.util.matching.Regex

case class Config(
                   foo: Int = -1,
                   myfiles : Seq[File] = dir(File(".")).toSeq,
                   out: JFile = new JFile("."),
                   noCommitsYet: Boolean = true,
                   libName: String = "",
                   maxCount: Int = -1,
                   verbose: Boolean = false,
                   isInitialized: Boolean = !File(PATH.SGIT).exists,
                   mode: String = "",
                   files: Seq[JFile] = Seq(),
                   addFiles : Seq[File] = Seq(),
                   keepAlive: Boolean = false,
                   jars: Seq[JFile] = Seq(),
                   index: Either[String, Index] = SGitUtils.getIndex(File(PATH.INDEX)),
                   filesUnTracked : Either[String, Seq[File]] = Left("No files to track"),
                   filesModified : Either[String, Seq[File]] = Left("No modified files"),
                   filesUnModified : Either[String, Seq[File]] = Left("No files to stage"),
                   filesToCommit : Either[String, Seq[File]]= Left("No files to commit"),
                   lastCommit : Option[Commit] = SGitUtils.getLastCommit,
                   regex: Regex = ".".r
                 )


object Parsor extends App {

  val builder = OParser.builder[Config]
  val parser1 = {
    import builder._
    OParser.sequence(
      programName("SGit"),
      head("SGit", "1.0"),

      cmd("init")
        .action((_, c) => c.copy(isInitialized = true, mode = "init"))
        .text("Create an empty SGit repository or reinitialize an existing one."),

      cmd("status")
        .action((_, c) => c.copy(
          mode = "status",
          filesUnTracked = Status.getFilesToTrack(c.index,
            c.myfiles.filter(file => !file.name.equals(".sgit")) ),
          filesToCommit =  Status.getFilesToCommit(c.index),
          filesModified = Status.getModifiedFiles(c.index)
        ))
        .text("Show the working tree status"),

      cmd("log")
        .action((_,c) => c.copy())
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
          .action((x, c) => if (x.isFile && x.exists()) c.copy(addFiles = c.addFiles :+ File(x.getAbsolutePath)) else c.copy(addFiles = c.addFiles ++ Status.getAllFilesFrom(Seq(File(x.getAbsolutePath)))) )
          .text("One or several files to add to the index")),

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
      if (config.isInitialized && !config.mode.equals("init")) {
        println(Console.RED + "fatal: not a sgit repository (or any of the parent directories): .sgit" + Console.RESET)
      } else {
        config.mode match {
          case "init" =>
            Creation.initSGitRepo()
            println(Console.GREEN + "Initialized empty SGit repository in " + File(PATH.SGIT).pathAsString + Console.RESET)

          case "add" =>
            config.index match {
              case Left(error) => println(error)
              case Right(index) =>

                IOUtils.writeBlobs(config.addFiles.filter(file => file.exists).map(file => Blob(file))) match {
                  case Some(maybeBlobs) =>
                    Addition.updateIndex(index, maybeBlobs) match {
                      case Some(newIndex) => IOUtils.writeIndex(newIndex)
                      case None => println("Error : index not updated.")
                    }
                  case None => println("No blobs files added.")
                }
            }

          case "status" =>
                println("On branch " + SGitUtils.getCurrentBranch()+"\n")

                config.filesToCommit match {
                  case Right(toCommitFiles) =>
                    IOUtils.printToCommitFiles(toCommitFiles)
                  case Left(_) => println("nothing to commit, working tree clean")
                }

                config.filesModified match {
                  case Right(modifiedFiles) => IOUtils.printToUpdateFiles(modifiedFiles)
                  case Left(_) =>
                }

                config.filesUnTracked match {
                  case Right(unTrackedFiles) =>
                    IOUtils.printUnTracked(unTrackedFiles)
                  case Left(_) =>
                }

          case "checkout" =>
            println(Console.YELLOW + "WORK IN PROGRESS" + Console.RESET)
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
            println(Console.YELLOW + "WORK IN PROGRESS" + Console.RESET)
          // create a new branch with given name "test"
          // create new file in .sgit/refs/heads/test
          // fill this file with current HEAD ref content

          case "commit" =>
            config.index match {
              case Left(error) => println(error)
              case Right(index) =>
                val newCommit = Commit(IOUtils.writeTree(SGitUtils.createTreeFromIndex(index)), config.lastCommit, "new Commit")
                IOUtils.writeCommitObject(newCommit)
                IOUtils.updateCurrentBranchCommit(newCommit)

            }

          case "reset" => if (File(PATH.SGIT).exists) File(PATH.SGIT).delete()

          case "log" =>

          case _ =>
            println(Console.YELLOW + "Internal error : not valid arguments" + Console.RESET)
        }
      }

    case None => println(Console.BLUE+"Internal Error : not a valid command"+Console.RESET)
  }
}
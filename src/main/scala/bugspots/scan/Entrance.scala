package bugspots.scan

import scala.collection.JavaConversions._
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.revwalk.RevWalk
import java.io.File
import java.nio.file.{Files, Paths}

import org.apache.commons.io.FileUtils
import org.apache.commons.validator.UrlValidator
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Repository

/**
  * Entrance
  */
object Entrance {
  def printList(args: List[_]): Unit = {
    args.foreach(println)
  }

  def allCommits(repository: Repository) = {
    val revWalk = new RevWalk(repository);
    val head = repository.exactRef("refs/heads/master")
    revWalk.markStart(
      revWalk.parseCommit(head.getObjectId())
    )

    revWalk.toList map (revComment => new BSCommit(revComment, repository))
  }

  def repo(repoDir: File): Repository = {
    new FileRepositoryBuilder()
      .setMustExist(true)
      .readEnvironment // scan environment GIT_* variables
      .findGitDir(repoDir) // scan up the file system tree
      .build
  }

  def main(args: Array[String]) = {
    println(args.toList)
    // e.g. C:/Users/user/teammates
    val location = args(0)

    val isUrl = new UrlValidator().isValid(location)
    if (isUrl) {
      require(location endsWith ".git")

      val localPath = Files.createTempDirectory(Paths.get(""), "BugspotClonedRepository")
//      localPath.toFile.deleteOnExit()

      val result = Git.cloneRepository()
        .setURI(location)
        .setDirectory(localPath.toFile)
        .call

      result.close
      println("cloned repository")
      printFilesAndScores(localPath.toFile)

      FileUtils.deleteDirectory(localPath.toFile)

    } else {
      printFilesAndScores(new File(args(0)))
    }

  }


  def printFilesAndScores(repoDir: File): Unit = {
    // TODO auto-closeable repository
    val repository: Repository = repo(repoDir)
    val commits = allCommits(repository)

    val bugspots = new Bugspots(repository, commits)
    printList(bugspots.filesAndScores)
    repository.close()
  }
}

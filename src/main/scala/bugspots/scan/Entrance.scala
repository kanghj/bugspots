package bugspots.scan

import scala.collection.JavaConversions._
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.revwalk.RevWalk
import java.io.File

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

    revWalk.toList
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
    val repoDir = new File(args(0))

    // TODO auto-closeable repository
    val repository: Repository = repo(repoDir)
    val commits = allCommits(repository)

    printList(new Bugspots(repository, commits).filesAndScores)
    repository.close()
  }
}

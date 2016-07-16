package bugspots.scan

import scala.collection.JavaConversions._
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.revwalk.RevWalk
import java.io.File

import org.eclipse.jgit.lib.Repository;

/**
  * Entrance
  */
object CLI {
  def main(args: Array[String]) = {
    println(args.toList)
    val repoDir = new File(args(0));

    // TODO auto-closeable repository
    val repository: Repository = repo(repoDir)

    val commits = allCommits(repository)
    val walk = new Walk(commits)

    val bugFixes = walk.buggyCommits

    bugFixes.map(bugfix => println(bugfix.getShortMessage))

    repository.close()
  }

  def allCommits(repository: Repository) = {
    val revWalk = new RevWalk(repository);
    val head = repository.exactRef("refs/heads/master");

    val commit = revWalk.parseCommit(head.getObjectId());
    revWalk.markStart(commit);

    revWalk.toList
  }

  def repo(repoDir: File): Repository = {
    val builder = new FileRepositoryBuilder();
    val repository = builder
      .setMustExist(true)
      .readEnvironment() // scan environment GIT_* variables
      .findGitDir(repoDir) // scan up the file system tree
      .build()
    println("Having repository: " + repository.getDirectory());
    repository
  }
}

package bugspots.scan

import scala.collection.JavaConversions._
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.revwalk.RevWalk
import java.io.File
import java.text.MessageFormat

import org.eclipse.jgit.diff.{DiffFormatter, RawTextComparator}
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.util.io.DisabledOutputStream;

/**
  * Entrance
  */
object Entrance {
  def main(args: Array[String]) = {
    println(args.toList)
    val repoDir = new File(args(0));

    // TODO auto-closeable repository
    val repository: Repository = repo(repoDir)

    val commits = allCommits(repository)

    println(new Bugspots(repository, commits).filesScore)
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
    new FileRepositoryBuilder()
      .setMustExist(true)
      .readEnvironment() // scan environment GIT_* variables
      .findGitDir(repoDir) // scan up the file system tree
      .build()
  }
}

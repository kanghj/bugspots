package bugspots.scan

import scala.collection.JavaConverters._
import scala.language.implicitConversions
import scala.collection.convert.WrapAsScala.enumerationAsScalaIterator
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.revwalk.RevWalk
import java.io.File
import java.nio.file.{Files, Paths}
import java.time.{Instant, LocalDate, ZoneId}
import java.util.Date

import org.apache.commons.io.FileUtils
import org.apache.commons.validator.UrlValidator
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Repository
import org.kohsuke.github.{GHCommit, GHIssueState, GitHub}

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

    revWalk.asScala.toList map (revComment => new BSCommit(revComment, repository))
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
      //gitClone(location)
      val githubName = location split("/") takeRight(2) mkString("/")
      printFilesAndScoresForGithub(githubName)
    } else {
      printFilesAndScoresForLocalRepo(new File(args(0)))
    }

  }


  def gitClone(location: String): Unit = {
    require(location endsWith ".git")

    val localPath = Files.createTempDirectory(Paths.get(""), "BugspotClonedRepository")
    //      localPath.toFile.deleteOnExit()

    val result = Git.cloneRepository()
      .setURI(location)
      .setDirectory(localPath.toFile)
      .call

    result.close
    println("cloned repository")
    printFilesAndScoresForLocalRepo(localPath.toFile)

    FileUtils.deleteDirectory(localPath.toFile)
  }

  def printFilesAndScoresForLocalRepo(repoDir: File): Unit = {
    // TODO auto-closeable repository
    val repository = repo(repoDir)
    val commits = allCommits(repository)

    printList(new Bugspots(commits).filesAndScores)
    repository.close()
  }

  def printFilesAndScoresForGithub(repoName: String) = {
    def commitsOnGithub(n : Int = 30) : List[BSCommit] = {

      val github = GitHub.connectAnonymously
      val repo = github.getRepository(repoName)

      val today = LocalDate.now
      val nDaysAgo = today.minusDays(n)

      val instant = Instant.from(nDaysAgo.atStartOfDay(ZoneId.of("GMT")))
      val todayInstant = Instant.from(today.atStartOfDay(ZoneId.of("GMT")))

      println(Date.from(instant))
      println(Date.from(todayInstant))
      val sample = repo.queryCommits
                       .since(Date.from(instant))
                       .until(Date.from(todayInstant))

      sample.list.asList.asScala.toList map (ghCommit => new BSCommit(ghCommit))

    }
    println("getting commits on github")
    val commits = commitsOnGithub(n=150)
    println("done getting commits")
    println(commits)

    printList(new Bugspots(commits).filesAndScores)
  }
}

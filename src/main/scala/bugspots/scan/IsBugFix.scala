package bugspots.scan

import org.eclipse.jgit.revwalk.RevCommit
import org.kohsuke.github.{GHIssueState, GitHub}

import scala.util.matching.Regex

/**
  * Created by user on 17/7/2016.
  */
trait IsBugFix {
  val commit: RevCommit
  def isBugFix : Boolean
}

class FromCommitMessage(val commit: RevCommit) extends IsBugFix {
  val pattern = new Regex("\\b(?i)(bug)|(fix(es|ed)?|close(s|d)?)\\b")

  def isBugFix() : Boolean = {
    val bugFixMatch = pattern findFirstIn commit.getFullMessage
    bugFixMatch match {
      case Some(s) => return true
      case None => return false
    }
  }
}

class FromGithubIssue(val commit: RevCommit, githubName: String) extends IsBugFix {
  def setupGithubConnection() = {
    val github = GitHub.connect();
    val repo = github.getRepository(githubName)

    repo.getPullRequests(GHIssueState.CLOSED)
  }
  def isBugFix() : Boolean = {
    ???
  }
}
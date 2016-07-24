package bugspots.scan

import org.eclipse.jgit.revwalk.RevCommit
import org.kohsuke.github.{GHIssueState, GitHub}

import scala.util.matching.Regex

trait IsBugFix {
  val commit: BSCommit
  def isBugFix : Boolean
}

class FromCommitMessage(val commit: BSCommit) extends IsBugFix {
  val pattern = new Regex("\\b(?i)(bug(s)?|error(s|ed)?|fail(s|ed)?|problem(s)?|patch(es|ed)?|fix(es|ed)?|close(s|d)?)\\b")

  def isBugFix() : Boolean = {
    val bugFixMatch = pattern findFirstIn commit.message
    bugFixMatch match {
      case Some(s) => return true
      case None => return false
    }
  }
}

class FromGithubIssue(val commit: BSCommit, githubName: String) extends IsBugFix {
  def setupGithubConnection() = {
    val github = GitHub.connect();
    val repo = github.getRepository(githubName)

    repo.getPullRequests(GHIssueState.CLOSED)
  }
  def isBugFix() : Boolean = {
    ???
  }
}
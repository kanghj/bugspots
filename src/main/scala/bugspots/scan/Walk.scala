package bugspots.scan

import scala.util.matching.Regex
import org.eclipse.jgit.revwalk.RevCommit
import org.kohsuke.github.{GHIssueState, GHRepository, GitHub};

class Walk(val commits: Seq[RevCommit]) {

  trait IsBugFix {
    val commit: RevCommit
    def isBugFix : Boolean
  }

  class FromCommitMessage(val commit: RevCommit) extends IsBugFix {
    val pattern = new Regex("\\b(?i)(fix(es|ed)?|close(s|d)?)\\b")

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

  def buggyCommits(): Seq[RevCommit] = {
    commits.filter(c => new FromCommitMessage(c).isBugFix)
  }
}

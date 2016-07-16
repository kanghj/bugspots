package bugspots.scan

import scala.util.matching.Regex
import org.eclipse.jgit.revwalk.RevCommit;

class Walk(val commits: Seq[RevCommit]) {

  val pattern = new Regex("\\b(?i)(fix(es|ed)?|close(s|d)?)\\b")

  def isBugFix(commit: RevCommit) : Boolean = {
    isBugFix(commit.getFullMessage)
  }

  def isBugFix(message: String) : Boolean = {
    val bugFixMatch = pattern findFirstIn message
    bugFixMatch match {
      case Some(s) => return true
      case None => return false
    }
  }

  def buggyCommits(): Seq[RevCommit] = {
    commits.filter(c => isBugFix(c))
  }
}


package bugspots.scan

import org.eclipse.jgit.revwalk.RevCommit

class Walk(val commits: Seq[RevCommit]) {

  def bugfixCommits(): Seq[RevCommit] = {
    commits.filter(c => new FromCommitMessage(c).isBugFix)
  }
}

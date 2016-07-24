package bugspots.scan

import org.eclipse.jgit.revwalk.RevCommit

class Walk(val commits: Seq[BSCommit]) {

  def bugfixCommits(): Seq[BSCommit] = {
    commits.filter(c => new FromCommitMessage(c).isBugFix)
  }
}

package bugspots.scan

import collection.JavaConverters._
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevCommit
import org.kohsuke.github.GHCommit

/**
  * Wraps over commit
  */
class BSCommit(val message:String, val filesAffected:Seq[String], val time: Long) {
  def this(revCommit: RevCommit, repository : Repository) {
    this(revCommit.getFullMessage,
      new FileNamesInRevCommit(repository, revCommit).get(),
      revCommit.getCommitTime)
  }

  def this(ghCommit: GHCommit) {
    this(ghCommit.getCommitShortInfo.getMessage,
      ghCommit.getFiles.asScala map (file => file.getFileName),
      ghCommit.getCommitShortInfo.getCommitter
              .getDate.getTime / 1000)
  }
}

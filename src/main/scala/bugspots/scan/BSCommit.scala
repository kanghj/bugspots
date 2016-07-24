package bugspots.scan

import collection.JavaConverters._
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevCommit
import org.kohsuke.github.GHCommit

/**
  * Wraps over commit
  */
class BSCommit(val message:String, val filesAffected:Seq[String]) {
  def this(revCommit: RevCommit, repository : Repository) {
    this(revCommit.getFullMessage,
         new FileNamesInRevCommit(repository, revCommit).get())
  }

  def this(ghCommit: GHCommit) {
    this(ghCommit.getCommitShortInfo.getMessage,
         ghCommit.getFiles.asScala map (file => file.getFileName))
  }
}

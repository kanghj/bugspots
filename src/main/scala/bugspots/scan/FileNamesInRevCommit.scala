package bugspots.scan

import collection.JavaConverters._
import org.eclipse.jgit.diff.{DiffFormatter, RawTextComparator}
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.{RevCommit, RevWalk}
import org.eclipse.jgit.util.io.DisabledOutputStream


class FileNamesInRevCommit(repository : Repository, commit: RevCommit) {
  def get() : List[String] = {
    if (commit.getParentCount <= 0) {
      return Nil
    }

    val rw = new RevWalk(repository)

    val parent = rw.parseCommit(commit.getParent(0).getId())

    val df = new DiffFormatter(DisabledOutputStream.INSTANCE)
    df.setRepository(repository)
    df.setDiffComparator(RawTextComparator.DEFAULT)
    df.setDetectRenames(true)

    df.scan(parent.getTree, commit.getTree)
      .asScala
      .toList
      .map(diff => diff.getOldPath)
      .filter(name => name != "/dev/null")
  }
}
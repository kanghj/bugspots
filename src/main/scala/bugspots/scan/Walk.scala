package bugspots.scan
import org.eclipse.jgit.api.Git;

class Walk {
  def buggyCommits(): Seq[Commits] = {
    ???
  }
}

class Commits(val message: String, val isBug: Boolean) {
  ???
}
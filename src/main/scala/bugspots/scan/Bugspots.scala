package bugspots.scan

import org.eclipse.jgit.revwalk.{RevCommit, RevWalk}

import org.eclipse.jgit.lib.Repository


/**
  *
  */
class Bugspots(repository : Repository, commits : Seq[BSCommit]) {
  val bugfixes = new Walk(commits).bugfixCommits
  val first = commits.sortBy(commit => commit.time).head
  val current = java.lang.System.currentTimeMillis() / 1000; // in seconds

  def filesAndScores = {
    def score(commit : BSCommit) = {
      def normalisedTime() : Double = {
        1 - ((current - commit.time).toDouble / (current - first.time))
      }
      1 / ( 1 + Math.exp((-12 * normalisedTime()) + 12))
    }

    val fixAndScoreAndFiles = bugfixes
      .map(fix => (fix, score(fix)))
      .map {case (fix, timeScore) => (fix, timeScore, fix.filesAffected)}

    val fileAndScore = for (
      (fix, score, files) <- fixAndScoreAndFiles;
      file <- files
    ) yield (file, score)

    fileAndScore
      .groupBy(_._1)
      .mapValues(_.map(_._2)
                  .sum
      ).toList
      .sortBy{case (file, score) => score}
      .reverse   // returns a sorted list of (filename, some sort of score?)
  }

}

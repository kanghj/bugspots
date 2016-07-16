package bugspots.scan

import collection.JavaConverters._
import org.eclipse.jgit.revwalk.{RevCommit, RevWalk}

import org.eclipse.jgit.diff.{DiffFormatter, RawTextComparator}
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.util.io.DisabledOutputStream


/**
  *
  */
class Bugspots(val repository : Repository, commits : Seq[RevCommit]) {
  val bugfixes = new Walk(commits).buggyCommits
  val first = commits.sortBy(commit => commit.getCommitTime).head
  val current = java.lang.System.currentTimeMillis() / 1000; // in seconds

  def normalisedTime(commit : RevCommit) : Long = {
    1 - ((current - commit.getCommitTime) / (current - first.getCommitTime))
  }

  def score(time: Long) = {
    1/(1+Math.exp((-12*time)+12))
  }

  def affectedFiles(commit: RevCommit) = {
    val rw = new RevWalk(repository)

    val parent = rw.parseCommit(commit.getParent(0).getId())

    val df = new DiffFormatter(DisabledOutputStream.INSTANCE)
    df.setRepository(repository)
    df.setDiffComparator(RawTextComparator.DEFAULT)
    df.setDetectRenames(true)

    df.scan(parent.getTree, commit.getTree).asScala
      .toList
      .map(diff => diff.getNewPath)
  }

  def filesScore = {

    val fixedTimeWithFiles = bugfixes
      .map(fix => (fix, score(normalisedTime(fix))))
      .map {case (fix, timeScore) => (fix, timeScore, affectedFiles(fix))}

    val fileCommitScore = for (
      (fix, score, files) <- fixedTimeWithFiles;
      file <- files
    ) yield (file, score)

    fileCommitScore
      .groupBy(_._1)
      .mapValues(_.map(_._2)
                .sum
      ).toList
      .sortBy{case (file, score) => score}
      .reverse   // returns a sorted list of (filename, some sort of score?)
  }



}

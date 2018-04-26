package io.prismic.homework.services

import io.prismic.homework.models.{Commit, UserImpact}

/**
  * Used to calculate analytics
  */
class AnalyticsService {
  /**
    * Based on a list of commit, process the number of commits by authors and order them by this number of commits (descending order)
    *
    * @param commits
    * @return
    */
  def processUsersImpact(commits: Seq[Commit]): Seq[UserImpact] = {
    val totalCommits = commits.size
    commits.groupBy(_.getAuthor()). map { byAuthorCommits =>
      val nbCommits = byAuthorCommits._2.size
      UserImpact(byAuthorCommits._1, nbCommits, toPercent(totalCommits, nbCommits))
    }.toSeq
  }

  private def toPercent(total: Int, number: Int): Float = {
    BigDecimal(100.0 * number / total).toFloat
  }
}

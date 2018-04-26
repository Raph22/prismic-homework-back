package services

import io.prismic.homework.models.{Commit, UserImpact}
import io.prismic.homework.services.AnalyticsService
import org.specs2.mutable.Specification

class AnalyticsServiceSpec extends Specification {

  "AnalyticsService" should {
    "process users impact for AHAAAAAAA/PokemonGo-Map" in {
      val seqCommits = Seq(
        Commit("600c0868ea5f21ac1e69e722487844673b5194f5", Some("AHAAAAAAA"), "Ahmed Almutawa", "2017-01-26T15:35:29Z"),
        Commit("77711d06a5bf0bcdf9ec9bcd8115e8eea9719529", Some("AHAAAAAAA"), "Ahmed Almutawa", "2016-08-12T18:21:59Z"),
        Commit("7159dc6e44239ffd022e500e16d00dc6662e2696", Some("AHAAAAAAA"), "Ahmed Almutawa", "2016-08-08T07:01:38Z"),
        Commit("8e9d4be1cd27f8432c2b528c8fed8403ef3c0649", Some("AHAAAAAAA"), "Ahmed Almutawa", "2016-08-06T18:44:43Z"),
        Commit("46064e49d1c636a4d459f4a5eabb7f12a5b192b2", Some("AHAAAAAAA"), "Ahmed Almutawa", "2016-07-16T15:06:34Z"))
      val expected = Seq(UserImpact("AHAAAAAAA", 5, 100))

      new AnalyticsService().processUsersImpact(seqCommits) mustEqual expected
    }
  }
}

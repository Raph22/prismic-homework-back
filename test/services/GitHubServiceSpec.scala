package services
import io.prismic.homework.models.{Commit, Contributor, Repository}
import io.prismic.homework.services.GitHubService
import org.specs2.mutable.Specification
import play.api.test.WsTestClient

import scala.concurrent.duration._
import scala.concurrent.Await

class GitHubServiceSpec extends Specification {

  "GitHubService" should {
    "search repositories for tetris" in {
      val expected = 200 -> Some(Seq(Repository("chvin", "react-tetris"), Repository("Aerolab", "blockrain.js"), Repository("jakesgordon", "javascript-tetris")))
      WsTestClient.withClient { client =>
        val result = Await.result(
          new GitHubService(client).search("tetris", Some(3)), 10.seconds)
          result must_=== expected
      }
    }

    "get contributors for AHAAAAAAA/PokemonGo-Map" in {
      val expected = 200 -> Some(Seq(Contributor("AHAAAAAAA")))
      WsTestClient.withClient { client =>
        val result = Await.result(
          new GitHubService(client).getContributors(Repository("AHAAAAAAA", "PokemonGo-Map")), 10.seconds)
        result must_=== expected
      }
    }

    "get latest commits for AHAAAAAAA/PokemonGo-Map" in {
      val expected = 200 -> Some(Seq(
        Commit("600c0868ea5f21ac1e69e722487844673b5194f5", Some("AHAAAAAAA"), "Ahmed Almutawa", "2017-01-26T15:35:29Z"),
        Commit("77711d06a5bf0bcdf9ec9bcd8115e8eea9719529", Some("AHAAAAAAA"), "Ahmed Almutawa", "2016-08-12T18:21:59Z"),
        Commit("7159dc6e44239ffd022e500e16d00dc6662e2696", Some("AHAAAAAAA"), "Ahmed Almutawa", "2016-08-08T07:01:38Z"),
        Commit("8e9d4be1cd27f8432c2b528c8fed8403ef3c0649", Some("AHAAAAAAA"), "Ahmed Almutawa", "2016-08-06T18:44:43Z"),
        Commit("46064e49d1c636a4d459f4a5eabb7f12a5b192b2", Some("AHAAAAAAA"), "Ahmed Almutawa", "2016-07-16T15:06:34Z")))
      WsTestClient.withClient { client =>
        val result = Await.result(
          new GitHubService(client).getLatestCommits(Repository("AHAAAAAAA", "PokemonGo-Map"), None), 10.seconds)
        result must_=== expected
      }
    }
  }
}
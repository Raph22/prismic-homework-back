package io.prismic.homework.services

import io.prismic.homework.models.{Commit, Contributor, Repository}
import javax.inject.Inject
import play.api.http.Status
import play.api.libs.json.JsArray
import play.api.libs.ws.{WSAuthScheme, WSClient, WSRequest}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Calls to GitHub API
  * @param ws
  */
class GitHubService @Inject()(ws: WSClient) {

  private val BASE_URL_API = "https://api.github.com"
  private val URL_API_SEARCH_REPO = "/search/repositories"
  private def URL_API_LIST_CONTRIBUTOR(owner: String, repository: String) = s"/repos/$owner/$repository/contributors"
  private def URL_API_LIST_COMMITS(owner: String, repository: String) = s"/repos/$owner/$repository/commits"

  private val HEADER_GITHUB_API = ("Accept" -> "application/vnd.github.v3+json")

  private val DEFAULT_NUMBER_PER_PAGE = 100
  private def PER_PAGE_PARAMETER(number: Int) = (s"per_page" -> number.toString)

  private val user = "XXX"
  private val password = "XXX"


  /**
    * Call GitHub API to retrieve repositories by a search on the name or description
    * @param searchTerm term to lookking for
    * @param numberPerPage (Optional) Number of results per page (default: 100)
    * @return
    */
  def search(searchTerm: String, numberPerPage: Option[Int]): Future[(Int, Option[Seq[Repository]])] = {
    val searchRequest: WSRequest = ws.url(BASE_URL_API + URL_API_SEARCH_REPO)
      .withHttpHeaders(HEADER_GITHUB_API)
      .withAuth(user, password, WSAuthScheme.BASIC)
      .addQueryStringParameters("q" -> searchTerm, PER_PAGE_PARAMETER(numberPerPage.getOrElse(100)))

    searchRequest.get.map { response =>
      response.status match {
        case Status.OK        => {
          if((response.json \ "items").as[JsArray].value.isEmpty) {
            Status.NOT_FOUND -> None
          } else {
            Status.OK -> Some((response.json \ "items").as[Seq[Repository]])
          }
        }
        case Status.NOT_FOUND => Status.NOT_FOUND -> None
        case _                => Status.INTERNAL_SERVER_ERROR -> None
      }
    }.recoverWith {
      case _: Exception => Future.successful(Status.INTERNAL_SERVER_ERROR -> None)
    }
  }

  /**
    * Call GitHub API to retrieve contributors on a repository
    * @param repository name of the repository on which we want to retrieve the list of contributors
    * @return
    */
  def getContributors(repository: Repository): Future[(Int, Option[Seq[Contributor]])] = { // Future[(Status, Option[Seq[Contributor]])]
    val contributorsRequest: WSRequest = ws.url(BASE_URL_API + URL_API_LIST_CONTRIBUTOR(repository.owner, repository.name))
      .withHttpHeaders(HEADER_GITHUB_API)
      .withAuth(user, password, WSAuthScheme.BASIC)

    contributorsRequest.get.map { response =>
      response.status match {
        case Status.OK        => Status.OK -> Some(response.json.as[Seq[Contributor]])
        case Status.NOT_FOUND => Status.NOT_FOUND -> None
        case Status.FORBIDDEN => Status.FORBIDDEN -> None
        case _                => Status.INTERNAL_SERVER_ERROR -> None
      }
    }.recoverWith {
      case _: Exception => Future.successful(Status.INTERNAL_SERVER_ERROR -> None)
    }
  }

  /**
    * Call GitHub API to retrieve latest commit on a repository
    * @param repository name of the repository on which we want to retrieve the latest commits
    * @param numberPerPage (Optional) Number of results per page (default: 100)
    * @return
    */
  def getLatestCommits(repository: Repository, numberPerPage: Option[Int]): Future[(Int, Option[Seq[Commit]])] = {
    val latestCommitsRequest: WSRequest = ws.url(BASE_URL_API + URL_API_LIST_COMMITS(repository.owner, repository.name))
      .withHttpHeaders(HEADER_GITHUB_API)
      .withAuth(user, password, WSAuthScheme.BASIC)
      .addQueryStringParameters(PER_PAGE_PARAMETER(numberPerPage.getOrElse(DEFAULT_NUMBER_PER_PAGE)))

    latestCommitsRequest.get.map { response =>
      response.status match {
        case Status.OK        => Status.OK -> Some (response.json.as[Seq[Commit]] )
        case Status.NOT_FOUND => Status.NOT_FOUND -> None
        case Status.FORBIDDEN => Status.FORBIDDEN -> None
        case _                => Status.INTERNAL_SERVER_ERROR -> None
      }
    }.recoverWith {
      case _: Exception => Future.successful(Status.INTERNAL_SERVER_ERROR -> None)
    }
  }
}

package io.prismic.homework.controllers

import io.prismic.homework.models.Repository
import io.prismic.homework.services.{AnalyticsService, GitHubService}
import javax.inject.Inject
import play.api.http.{Status => HttpStatus}
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global


/**
  * MainController is the main controller ;-)
  * @param cc
  * @param gitHubService
  * @param analyticsService
  */
class MainController @Inject()(cc: ControllerComponents)(gitHubService: GitHubService, analyticsService: AnalyticsService) extends AbstractController(cc) {


  /**
    * Called when a request is done for a search of repositories
    * @param searchTerm search term to looking for
    * @return
    */
  def search(searchTerm: String, per_page: Option[Int]) = Action.async { implicit request =>
    gitHubService
      .search(searchTerm, per_page)
      .map {
        case (_, Some(repositories)) => Ok(Json.toJson(repositories)).withHeaders("Access-Control-Allow-Origin" -> "*")
        case (status, None)          => processBadStatus(status)
      }
  }


  /**
    * Called when a request is done to retrieve the list of contributors of a repository
    * @param owner owner's pseudo of the repository on which we retrieve the list of contributors
    * @param repo name of the repository on which we retrieve the list of contributors
    * @return
    */
  def getContributors(owner: String, repo: String) = Action.async { implicit request =>

    gitHubService
      .getContributors(Repository(owner, repo))
      .map {
        case (_, Some(contributors)) => Ok(Json.toJson(contributors)).withHeaders("Access-Control-Allow-Origin" -> "*")
        case (status, None)          => processBadStatus(status)
      }
  }


  /**
    * Called when a request is done to retrieve the latest commits of a repository
    * @param owner owner's pseudo of the repository on which we retrieve the latest commits
    * @param repo name of the repository on which we retrieve the list of contributors
    * @return
    */
  def getLatestCommits(owner: String, repo: String, per_page: Option[Int]) = Action.async { implicit request =>
    gitHubService
      .getLatestCommits(Repository(owner, repo), per_page)
      .map {
        case (_, Some(commits)) => Ok(Json.toJson(commits)).withHeaders("Access-Control-Allow-Origin" -> "*")
        case (status, None)     => processBadStatus(status)
      }
  }


  /**
    *
    * @param owner
    * @param repo
    * @return
    */
  def getUsersImpact(owner: String, repo: String) = Action.async { implicit request =>
    gitHubService
      .getLatestCommits(Repository(owner, repo), None)
      .map {
        case (_, Some(commits)) => Ok(Json.toJson(analyticsService.processUsersImpact(commits))).withHeaders("Access-Control-Allow-Origin" -> "*")
        case (status, None)     => processBadStatus(status)
      }
  }


  /**
    * Send response for bad status
    * @param status
    * @return
    */
  private def processBadStatus(status: Int) = {
    status match {
      case HttpStatus.NOT_FOUND => NotFound.withHeaders("Access-Control-Allow-Origin" -> "*")
      case HttpStatus.FORBIDDEN => Forbidden.withHeaders("Access-Control-Allow-Origin" -> "*")
      case _                    => InternalServerError.withHeaders("Access-Control-Allow-Origin" -> "*")
    }
  }
}

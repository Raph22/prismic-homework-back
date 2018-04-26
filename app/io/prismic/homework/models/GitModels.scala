package io.prismic.homework.models

import play.api.libs.functional.syntax._
import play.api.libs.json._


case class Repository(owner: String, name: String)

object Repository {
  implicit val repositoryReads: Reads[Repository] = (
    (JsPath \ "owner" \ "login").read[String] and
      (JsPath \ "name").read[String]
    ) (Repository.apply _)

  implicit val repositoryWrites = new Writes[Repository] {
    def writes(repository: Repository) = Json.obj(
      "owner" -> repository.owner,
      "name" -> repository.name
    )
  }
}

case class Contributor(name: String)

object Contributor {
  implicit val contributorReads: Reads[Contributor] = Reads {
    js => JsSuccess(Contributor((js \ "login").as[String]))
  }

  implicit val contributorWrites = new Writes[Contributor] {
    def writes(contributor: Contributor) = Json.obj(
      "name" -> contributor.name
    )
  }
}

case class Commit(sha: String, authorPseudo: Option[String], authorRealName: String, date: String) {
  def getAuthor(): String = {
    authorPseudo.getOrElse(authorRealName)
  }
}

object Commit {
  implicit val commitReads: Reads[Commit] = (
    (JsPath \ "sha").read[String] and
      (JsPath \ "author" \ "login").readNullable[String] and // could be null if the user is not on GitHub anymore
      (JsPath \ "commit" \ "author" \ "name").read[String] and
      (JsPath \ "commit" \ "author" \ "date").read[String]

    ) (Commit.apply _)

  implicit val commitWrites = new Writes[Commit] {
    def writes(commit: Commit): JsObject = Json.obj(
      "sha" -> commit.sha,
      "author" -> commit.getAuthor(),
      "date" -> commit.date
    )
  }
}

case class UserImpact(author: String, number: Float, percent: Float)

object UserImpact {
  implicit val userImpactWrites = new Writes[UserImpact] {
    def writes(userImpact: UserImpact): JsObject = Json.obj(
      "author" -> JsString(userImpact.author),
      "number" -> userImpact.number,
      "percent" -> userImpact.percent
    )
  }
}
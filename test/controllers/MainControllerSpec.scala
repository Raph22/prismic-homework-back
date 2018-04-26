package controllers

import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.libs.json.Json
import play.api.test._
import play.api.test.Helpers._

class MainControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {

  "MainController" should {
    "search tetris" in {
      val fakeRequest = FakeRequest(GET, "/api/search/tetris?per_page=3")
      val result = route(app, fakeRequest).get
      val expected =
        s"""
           |[
           |  {
           |    "owner": "chvin",
           |    "name": "react-tetris"
           |  },
           |  {
           |    "owner": "Aerolab",
           |    "name": "blockrain.js"
           |  },
           |  {
           |    "owner": "jakesgordon",
           |    "name": "javascript-tetris"
           |  }
           |]
         """.stripMargin

      contentAsJson(result) mustEqual Json.parse(expected)
    }
  }

}

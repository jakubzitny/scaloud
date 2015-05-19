package logic.gitlab

import java.net.URL

import _root_.models.GitlabSession
import com.github.tototoshi.play2.json4s.native.Json4s
import org.json4s.{Extraction, DefaultFormats}
import play.api.Logger
import play.api.libs.ws.{WS, WSResponse}
import play.api.mvc.Controller

import scala.concurrent.Future
import play.api.Play.current

/**
 * TODO: finish
 */
class GitLabCiApi(gitLabCiUrl: String, gitLabUrl: String, gitLabToken: String) extends Controller with Json4s {
  // To allow json4s deserialization through Extraction.decompose
  implicit val formats = DefaultFormats

  // Implicit context for the Play Framework
  implicit val context = play.api.libs.concurrent.Execution.Implicits.defaultContext

  val logger = Logger("GitlabCiAPI")
  val authToken = "PRIVATE-TOKEN" -> gitLabToken

  def getAPIUrl(tailAPIUrl: String): URL = {
    var fullUrl = tailAPIUrl
    fullUrl = tailAPIUrl + (if (tailAPIUrl.indexOf('?') > 0) '&' else '?') + "private_token=" + gitLabToken + "&url=" + gitLabUrl
    if (!fullUrl.startsWith("/"))
      fullUrl = "/" + fullUrl
    new URL(gitLabCiUrl + fullUrl)
  }

  // ci api doc is old:
  // TODO: https://github.com/gitlabhq/gitlab-ci/blob/master/lib/api/projects.rb
  def createProject(name: String) = {
    WS.url(gitLabCiUrl + "/projects/" + name).withHeaders(authToken).get()
  }

}
package logic.gitlab

import java.net.URL

import _root_.models.{CiProject, Project, GitlabSession}
import com.github.tototoshi.play2.json4s.native.Json4s
import org.json4s.{Extraction, DefaultFormats}
import play.api.Logger
import play.api.libs.ws.{WS, WSResponse}
import play.api.mvc.Controller

import scala.concurrent.Future
import play.api.Play.current

/**
 * gitlab ci api requests
 * derived from gitlab api
 *
 * @see http://goo.gl/aG9wC6
 *
 * @author Jakub Zitny <zitnyjak@fit.cvut.cz>
 * @since Mon May 18 05:15:21 CEST 2015
 */
class GitLabCiApi(gitLabCiUrl: String, gitLabUrl: String, gitLabToken: String) extends Controller with Json4s {

  implicit val formats = DefaultFormats
  implicit val context = play.api.libs.concurrent.Execution.Implicits.defaultContext

  val logger = Logger("GitlabCiAPI")

  val authToken = "PRIVATE-TOKEN" -> gitLabToken
  val uriSuffix = "?gitlab_url=" + gitLabUrl

  def getAPIUrl(tailAPIUrl: String): URL = {
    var fullUrl = tailAPIUrl
    fullUrl = tailAPIUrl + (if (tailAPIUrl.indexOf('?') > 0) '&' else '?') + "private_token=" + gitLabToken + "&url=" + gitLabUrl
    if (!fullUrl.startsWith("/"))
      fullUrl = "/" + fullUrl
    new URL(gitLabCiUrl + fullUrl)
  }

  /**
   * GET /projects
   */
  def getProjects = {
    WS.url(gitLabCiUrl + "/projects").withHeaders(authToken).get()
  }

  /**
   * POST /projects
   *
   * ci api doc is old
   * @see http://git.io/vTgE5 for current version
   *
   * @param name duh
   * @param gitlabId duh
   * @param gitlabPath duh
   * @param sshUrlToRepo duh
   * @return future with WSResponse data
   */
  def createProject(name: String, gitlabId: String, gitlabPath: String, sshUrlToRepo: String) = {
    WS.url(gitLabCiUrl + "/projects" + uriSuffix).withHeaders(authToken).post(Extraction.decompose(
      CiProject(
        name,
        gitlabId,
        gitlabPath,
        sshUrlToRepo
      )).underscoreKeys)
  }

  //def postJob() = {
  //  // https://github.com/gitlabhq/gitlab-ci/blob/master/lib/api/projects.rb#L57
  //}

}
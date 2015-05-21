package services

import exceptions.GitLabCiCreateException
import logic.gitlab.{GitLabCiApi, GitLabApi}
import models.{GitLabCiProject, GitLabProject}
import play.api.Logger
import play.api.http.Status
import play.api.libs.json._

//import scala.concurrent.ExecutionContext.Implicits.global
//import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.concurrent.Execution.Implicits

/**
 * wrapper class for communicating with GitLab CI API
 *
 * @author Jakub Zitny <zitnyjak@fit.cvut.cz>
 * @since Wed May 20 18:18:13 CEST 2015
 */
object GitLabCiService {

  val GitLabUrl = "https://gitlab.com/api/v3/"
  val GitLabCiUrl = "https://ci.gitlab.com/api/v1/"
  val GitLabToken = "t-n4vX1mArhpvoumsj14"

  val gitLabCiApi = new GitLabCiApi(GitLabCiUrl, GitLabUrl, GitLabToken)
  val logger = Logger(this.getClass.getSimpleName)

  implicit val context = Implicits.defaultContext

  /**
   * creates project of given name on GitLab CI
   *
   * @see http://goo.gl/aG9wC6
   *
   * @param project previously created GitLabProject
   * @return wrapped future with actions after project is created
   */
  def createProject(project: GitLabProject) = {
    gitLabCiApi.createProject(project.nameWithNameSpace, project.id,
        project.pathWithNameSpace, project.sshUrlToRepo).map { response =>
      val ciProject = formatProjectResponse(response.json.as[JsObject])
      Logger.info("createCiProject: " + ciProject)
      response.status match {
        case Status.CREATED => ciProject
        case _ => throw new GitLabCiCreateException
      }
    }
  }

  /**
   * retrieves projects from CI
   *
   * @return empty future
   */
  def getProjects = {
    gitLabCiApi.getProjects.map { response =>
      println(response.body)
    }
  }

  /**
   * parse the response
   *
   * @param projectJson json of created ci project
   * @return GitLabCiProject of created project
   */
  private def formatProjectResponse(projectJson: JsObject): GitLabCiProject = {
    new GitLabCiProject((projectJson \ "id").as[Long],
      (projectJson \ "name").as[String], (projectJson \ "token").as[String])
  }


}
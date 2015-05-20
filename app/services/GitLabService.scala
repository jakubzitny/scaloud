package services

import exceptions._
import logic.gitlab.GitLabApi
import models.{GitLabCiProject, GitLabProject, Project}
import play.api.Logger
import play.api.libs.json._
import play.api.libs.ws.{WS, WSRequestHolder, WSResponse}
import scala.concurrent.{ExecutionContext, Future}
import scala.async.Async.{async, await}
import scala.util.{Failure, Success}
import play.api.http._

//import scala.concurrent.ExecutionContext.Implicits.global
//import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.concurrent.Execution.Implicits

/**
 * wrapper class for communicating with GitLab API
 *
 * @author Jakub Zitny <zitnyjak@fit.cvut.cz>
 * @since Tue May 12 03:54:56 CEST 2015
 */
object GitLabService {

  implicit val context = Implicits.defaultContext

  val GitLabUrl = "https://gitlab.com/api/v3/"
  val GitLabToken = "t-n4vX1mArhpvoumsj14"

  val gitLabApi = new GitLabApi(GitLabUrl, GitLabToken)
  val logger = Logger(this.getClass.getSimpleName)

  /**
   * requests all projects on GitLab
   * the response from api has json array of projects
   *  - convert the json array to List of JsObjects
   *  - map only "id" from each project as Int
   * @return Future with List of all project IDs
   */
  def getProjects = {
    gitLabApi.getProjects().map { response =>
      val projects = response.json.as[List[JsObject]]
      projects.map(p => (p \ "id").as[Int])
    }
  }

  /**
   * creates project of given name on GitLab
   *
   * @see http://git.io/vTgW6#create-project
   *
   * @param name the name of creating repo
   * @return Future with (name, project url) tuple or Exception
   */
  def createProject(name: String) = {
    gitLabApi.createProject(name = name, public = Some(true)).map { response =>
      val project = formatProjectResponse(response.json.as[JsObject])
      logger.info("createProject: " + project)
      response.status match {
        case Status.CREATED => project
        case _ => throw new GitLabCreateException
      }
    }
  }

  /**
   * enables ci service on gitlab project
   * links ci project with gitlab project
   *
   * @see http://git.io/vTgWN#edit-gitlab-ci-service
   *
   * @param project previously created GitLabProject
   * @param ciProject previously created GitLabCiProject
   * @return future with useless data
   */
  def enableCi(project: GitLabProject, ciProject: GitLabCiProject) = {
    gitLabApi.enableServiceCi(project.id, ciProject.token, ciProject.projectUrl).map { response =>
      logger.info("enableCi: " + response.body)
      response.status match {
        case Status.OK => response.body
        case _ => throw new GitLabEnableCiException
      }
    }
  }

  /**
   * parse project name and ssh url to repo from project json
   *
   * @param projectJson json of created project
   * @return GitLabProject data of created project
   */
  private def formatProjectResponse(projectJson: JsObject): GitLabProject = {
    new GitLabProject((projectJson \ "id").as[Long], (projectJson \ "name").as[String],
      (projectJson \ "ssh_url_to_repo").as[String], (projectJson \ "name_with_namespace").as[String])
  }

}

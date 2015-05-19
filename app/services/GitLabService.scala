package services

import logic.gitlab.GitLabApi
import models.Project
import play.api.libs.json._
import play.api.libs.ws.{WS, WSRequestHolder, WSResponse}
import scala.concurrent.{ExecutionContext, Future}
import scala.async.Async.{async, await}
import scala.util.{Failure, Success}
import org.json4s._
import org.json4s.native.JsonMethods._

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

  val GitLabUrl = "https://gitlab.com/api/v3/"
  val GitLabToken = "t-n4vX1mArhpvoumsj14"
  val gitLabApi = new GitLabApi(GitLabUrl, GitLabToken)

  implicit val context = Implicits.defaultContext

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
   * TODO: log stuff
   * @param name the name of creating repo
   * @return Future with (name, project url) tuple or Exception
   */
  def createProject(name: String) = {
    gitLabApi.createProject(name = name, public = Some(true)).map { response =>
      println(response.body)
      response.status match {
        case 201 => formatProjectResponse(response.json.as[JsObject])
        case _ => throw new Exception("failed")
      }
    }
  }

  /**
   * parse project name and ssh url to repo from project json
   * TODO: make a GitLabProject class
   * @param projectJson json of created project
   */
  private def formatProjectResponse(projectJson: JsObject): (String, String) = {
    ((projectJson \ "name").as[String], (projectJson \ "ssh_url_to_repo").as[String])
  }

  /* TODO
  private def formatProject(body: String): Project = {
    val json = parse(body)
    val project: Project = json.extract[Project]
    println(project)
    project
  }*/

}

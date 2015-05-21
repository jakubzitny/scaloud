//
//  Created by Alejandro Barros Cuetos <jandrob1978@gmail.com>
//
//  Redistribution and use in source and binary forms, with or without
//  modification, are permitted provided that the following conditions are met:
//
//  1. Redistributions of source code must retain the above copyright notice, this
//  list of conditions and the following disclaimer.
//
//  2. Redistributions in binary form must reproduce the above copyright notice,
//  this list of conditions and the following disclaimer in the documentation
//  && and/or other materials provided with the distribution.
//
//  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
//  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
//  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
//  ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
//  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
//  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
//  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
//  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
//  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
//  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
//  POSSIBILITY OF SUCH DAMAGE.
//

package controllers

import _root_.services.{GitLabCiService, GitLabService, DockerService}
import com.github.tototoshi.play2.scalate._
import logic.AppManager
import logic.gitlab.GitLabCiApi
import play.api.Logger
import play.api.mvc.{Action, RequestHeader}
import securesocial.core._
import models.DemoUser
import scala.concurrent.Future
import scala.util.{Failure, Success}

/**
 * base controller for whole project
 *
 * @author Jakub Zitny <zitnyjak@fit.cvut.cz>
 * @since Thu May  7 00:51:31 CEST 2015
 */
class Application(override implicit val env: RuntimeEnvironment[DemoUser])
  extends securesocial.core.SecureSocial[DemoUser] {

  /**
   * /
   *
   * @return rendered index page
   */
  def index = Action { implicit request =>
    Ok(Scalate.render("index.jade", Map()))
  }

  /**
   * /apps
   *
   * @return rendered create apps page
   */
  def apps = SecuredAction.async { implicit request =>
    GitLabService.getProjects.map { response =>
      Ok(Scalate.render("apps.jade", Map(
        "projects" -> response.map(project =>
          (project.id.toInt, project.name, project.webUrl)) // hack for stupid jade
      )))
    }
  }

  /**
   * /app/:id
   *
   * @return rendered create app detail page
   */
  def app(id: String) = Action.async { implicit request =>
    GitLabService.getProject(id.toLong).map { project =>
      Ok(Scalate.render("app.jade", Map(
        "app" -> (project.name, project.httpUrlToRepo)
      )))
    }
  }

  /**
   * @return rendered create app page
   */
  def create = SecuredAction.async { implicit request =>
    AppManager.createProject.map { response =>
      Redirect(routes.Application.app(response.id.toString))
    } recover {
      case cause => Ok("fail " + cause)
    }
  }

  def docker = SecuredAction { implicit request =>
    DockerService.test()
    Ok(views.html.docker(request.user.main))
  }

  def admin = SecuredAction.async { request =>
    GitLabService.getProjects.map { response =>
      Ok(Scalate.render("admin.jade", Map(
        "projects" -> response.map(project =>
          (project.id.toInt, project.name, project.webUrl)) // hack for stupid jade
      )))
    }
  }

  def gitLabRemove(id: String) = SecuredAction.async { request =>
    AppManager.deleteProject(id.toLong).map { response =>
      Redirect(routes.Application.admin())
    }
  }

  def futureTest = Action { request =>
    //val s = DockerService.sshDeployCmd("https://gitlab.com/jakubzitny/scaloud_app_as84awuz.git")
    //println(s)
    //val s = GitLabCiService.createProject("testname3", 276883L, "jakubzitny/uxy3v3ei", "git@gitlab.com:jakubzitny/uxy3v3ei.git")
    //val s = GitLabService.enableCi(278556, 2923, "62eb81c7c2168132e46a88ba01838b")
    Ok(this.getClass.getSimpleName)
  }

}

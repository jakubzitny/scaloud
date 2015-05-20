package logic

import services._
import play.api.libs.concurrent.Execution._

/**
 * main logic class managing created apps
 *
 * @author Jakub Zitny <zitnyjak@fit.cvut.cz>
 * @since Mon May 18 00:51:31 CEST 2015
 */
object AppManager {

  implicit val context = Implicits.defaultContext

  def createProject = {
    val name = StringUtility.generateAppName()
    for {
      // create in gl and glci
      project <- GitLabService.createProject(name)
      ciProject <- GitLabCiService.createProject(project)
      success <- GitLabService.enableCi(project, ciProject)
      // prepare docker container
      docker <- DockerService.createContainer(project.name, project.sshUrlToRepo)
      // save to db
    } yield project
  }

}

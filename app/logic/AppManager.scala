package logic

import services.GitLabService

/**
 * Created by d_rc on 18/05/15.
 */
object AppManager {

  implicit val context = play.api.libs.concurrent.Execution.Implicits.defaultContext

  def createProject = {
    val name = StringUtility.generateAppName()
    // create in gl
    GitLabService.createProject(name)
    // enable ci integration
    // prepare docker container
    // save to db
    // return
  }

}

package services

import logic.GitLabApi
import play.api.libs.ws.WSResponse
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Created by d_rc on 17/05/15.
 */
class GitLabService {

  val GitLabUrl = "https://gitlab.com/"
  val GitLabToken = "t-n4vX1mArhpvoumsj14"

  def test = {
    val gitLabApi = new GitLabApi(GitLabUrl, GitLabToken)

    val futureResult: Future[String] = gitLabApi.getProjects().map {
      response =>
        (response.json \ "person" \ "name").as[String]
    }
    println(futureResult)
  }


}

package models

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class CiProject(name: String,
                   gitlabId: String,
                   gitlabPath: String,
                   sshUrlToRepo: String)

object CiProject  {
  implicit val gitlabUserReader: Reads[CiProject] = (
    (__ \ "name").read[String] and
      (__ \ "gitlab_id").read[String] and
      (__ \ "gitlab_path").read[String] and
      (__ \ "ssh_url_to_repo").read[String]
    )(CiProject.apply _)
}

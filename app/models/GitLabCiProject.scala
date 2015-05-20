package models

import services.GitLabCiService

/**
 * GitLabProject model class
 * @author Jakub Zitny <zitnyjak@fit.cvut.cz>
 * @since Thu May 21 00:30:46 CEST 2015
 */
case class GitLabCiProject (id: Long, name: String, token: String) {
  val CiProjectUrlPrefix = "https://ci.gitlab.com/projects/"
  def projectUrl = CiProjectUrlPrefix + id
}
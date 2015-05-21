package models

/**
 * GitLabProject model class
 * @author Jakub Zitny <zitnyjak@fit.cvut.cz>
 */
case class GitLabProject (id: Long, name: String, sshUrlToRepo: String, httpUrlToRepo: String,
                          nameWithNameSpace: String, pathWithNameSpace: String, webUrl: String)
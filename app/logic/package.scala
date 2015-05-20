/**
 * @author Jakub Zitny <zitnyjak@fit.cvut.cz>
 * @since Thu May 21 00:23:04 CEST 2015
 */
package object exceptions {
  class GitLabCreateException() extends Exception("GitLabCreate") { }
  class GitLabCiCreateException() extends Exception("GitLabCiCreate") { }
  class GitLabEnableCiException() extends Exception("GitLabEnableCi") { }
}

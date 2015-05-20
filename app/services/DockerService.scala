package services

import java.util.concurrent.TimeUnit

import com.decodified.scalassh.SSH
import com.kolor.docker.api._
import com.kolor.docker.api.json.FormatsV112._
import com.kolor.docker.api.entities._
import play.Logger
import scala.concurrent.duration.Duration
import scala.concurrent.Await
import play.api.libs.iteratee._
import sys.process._
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * service providing connection to docker instance
 *
 * @author Jakub Zitny <zitnyjak@fit.cvut.cz>
 * @since Thu May  7 00:51:31 CEST 2015
 */
object DockerService {

  val DockerHost = "178.77.239.132"
  val DockerPort = "12345"
  val PlayPort = 9000
  val DockerBaseImage = "scaloud/play"

  implicit val docker = Docker(DockerHost + ":" + DockerPort)

  /**
   * creates docker container for given app repo
   *
   * @param appName the name of app (and also the docker)
   * @param appRepo the repository url of the app
   * @return future with wrapped docker container creation stuff
   */
  def createContainer(appName: String, appRepo: String) = {
    val imageTag = RepositoryTag.create(DockerBaseImage, Some("latest"))
    //val cfg = ContainerConfig(DockerBaseImage, appDaemon(appName, appRepo))
    val portBindingMap = Map("wtfisthis" -> DockerPortBinding(9010, Option(9009)))
    val cfg = ContainerConfiguration(image = Option(DockerBaseImage), tty = Option(true), exposedPorts = Option(portBindingMap))

    for {
      image <- docker.imageCreate(imageTag)
      container <- docker.containerCreate(DockerBaseImage, cfg, Some(appName))
      //tmp <- sshDeploy(appRepo)
      success <- docker.containerStart(container._1)
    } yield deploySequence(container._1.toString(), appRepo)
  }

  private def deploySequence(containerId: String, appRepo: String) = {
    //sshCreateCmd(appName)
    sshDeployCmd(containerId, appRepo)
  }

  /**
   * temp hack to easily deploy app without docker commmits
   *
   * @param appName the name of app
   * @param appRepo the https url of repositry
   * @return Seq daemon command
   */
  private def appDaemon(appName: String, appRepo: String) = {
    val cmd = "cd /home;" +
      "git clone " + appRepo + ";" +
        "cd " + appName + ";" +
          "sbt run"
    Seq("/bin/sh/", "-c", cmd)
  }

  /**
   * docker create hack via ssh
   *
   * @param name the name of container to create
   * @return nothing important
   */
  def sshCreateCmd(name: String) = {
    val cmd =
      "docker -H=" + DockerHost + ":" + DockerPort + " " +
        "run -i -t -p 9000:9000 -name " + name + " 70149e0c9fc5"
    println("executing: " + cmd)
    val sshCmd = "ssh " + DockerHost + " " + cmd
    val stdout = new StringBuilder
    val stderr = new StringBuilder
    val status = sshCmd ! ProcessLogger(stdout append _, stderr append _)
    println("s: " + status)
    println("o: " + stdout)
    println("e: " + stderr)
    stdout.toString()
  }

  /**
   * docker deploy hack via ssh
   *
   * @param containerId the id of container to handle
   * @param appRepo the repo of app to deploy inside
   * @return nothing important
   */
  def sshDeployCmd(containerId: String, appRepo: String) = {
    val cmd =
      "docker -H=" + DockerHost + ":" + DockerPort + " " +
        "exec -d " + containerId + " " +
        "bash -c \"" +
          "git remote set-url origin " + appRepo + ";" +
          "git fetch origin;" +
          "git reset --hard origin/master\""
    println("executing: " + cmd)
    "ssh " + DockerHost + " " + cmd !!
  }

  /**
   * rests the docker redeploy strategy
   * @param appRepo string of a repo to deploy inside container
   */
  def sshDeployLib(appRepo: String) {
    SSH(DockerHost) { client =>
      println(appRepo)
      client.exec("mkdir /tmp/asd").right.map { result =>
        println("Result:\n" + result.stdOutAsString())
      }
    }
  }

  /**
   * tests the docker daemon with busybox container
   */
  def test() = {
    val timeout = Duration.create(30, TimeUnit.SECONDS)

    val cmd = Seq("/bin/sh", "-c", "while true; do echo hello world; sleep 1; done")
    val containerName = "reactive-docker"
    val imageTag = RepositoryTag.create("busybox", Some("latest"))
    val cfg = ContainerConfig("busybox", cmd)


    // create image, returns a list of docker messages when finished
    val messages = Await.result(docker.imageCreate(imageTag), timeout)

    messages.map(m => println(s"imageCreate: $m"))

    // create container
    val containerId = Await.result(docker.containerCreate("busybox", cfg, Some(containerName)), timeout)._1

    // run container
    Await.result(docker.containerStart(containerId), timeout)
    println(s"container $containerId is running")
    Logger.info(s"container $containerId is running")
  }

}




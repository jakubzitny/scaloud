package services

import java.util.concurrent.TimeUnit

import com.kolor.docker.api._
import com.kolor.docker.api.json.FormatsV112._
import com.kolor.docker.api.entities.{ContainerConfig, RepositoryTag}
import play.Logger
import scala.concurrent.duration.Duration
import scala.concurrent.Await
import play.api.libs.iteratee._
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by d_rc on 17/05/15.
 */
class DockerService {

  val dockerHost = "178.77.239.132:12345"

  def test = {
    implicit val docker = Docker(dockerHost)
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




package controllers

import java.util.UUID

import dao.ImageOperationDAO
import models.ImageOperation
import play.api.mvc._
import java.io.File
import play.api.libs.json.Json._
import akka.actor.{Props, ActorSystem}
import actors.ImageSupervisor
import actors.ImageSupervisor.HandleOperation
import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject.{Singleton, Inject}

@Singleton
class GreyscaleController @Inject() (dao: ImageOperationDAO) extends Controller {
  lazy val imageHandler = ActorSystem("Greyscalr").actorOf(Props(classOf[ImageSupervisor], dao), name = "ImageHandler")

  def list() = Action.async { request =>
    dao.list().map { jsonList =>
      Ok(toJson(jsonList).toString)
    }
  }

  def create() = {
    Action(parse.multipartFormData) { request =>
      val files = request.body.files
      if (files.isEmpty) {
        BadRequest("You should upload at least one file")
      }
      else {
        val newId = UUID.randomUUID().toString
        val fullPath = "/tmp/upload-" + newId.toString + ".original"

        files.head.ref.moveTo(new File(fullPath))
        val newOperation = new ImageOperation(newId)
        dao.insert(newOperation)
        imageHandler ! HandleOperation(newId.toString, fullPath)

        Created(toJson(newOperation))
      }
    }
  }

  def getStatus(id: String) = Action.async { request =>
    dao.find(id).map {
      _ match {
        case Some(status) => Ok(toJson(status))
        case None => NotFound("Can't find element")
      }
    }
  }



}

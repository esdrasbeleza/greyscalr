package controllers

import play.api.mvc._
import java.io.File
import models.ImageOperation
import play.api.libs.json.Json._
import org.bson.types.ObjectId
import akka.actor.{Props, ActorSystem}
import actors.ImageSupervisor
import actors.ImageSupervisor.HandleOperation

object GreyscaleController extends Controller {
  lazy val imageHandler = ActorSystem("Greyscalr").actorOf(Props[ImageSupervisor], name = "ImageHandler")

  def list() = Action { request =>
    val jsonList = ImageOperation.list().map(toJson(_)).toSeq
    Ok(toJson(jsonList))
  }

  def create() = {
    Action(parse.multipartFormData) { request =>
      val files = request.body.files
      if (files.isEmpty) {
        BadRequest("You should upload at least one file")
      }
      else {
        val id = new ObjectId()
        val fullPath = "/tmp/upload-" + id.toString + ".original"

        files.head.ref.moveTo(new File(fullPath))
        val status = ImageOperation.create(id.toString)
        imageHandler ! HandleOperation(id.toString, fullPath)

        Created(toJson(status))
      }
    }
  }

  def getStatus(id: String) = {
    Action { request =>
      try {
        val status = ImageOperation.read(id)
        Ok(toJson(status))
      }
      catch {
        case e: NoSuchElementException => NotFound("Can't find element")
      }
    }
  }


}

package actors

import akka.actor.{Props, ActorSystem, Actor}
import actors.ImageSupervisor.{OperationError, UploadDone, ImageReady, HandleOperation}
import dao.ImageOperationDAO
import actors.ImageEditor.ConvertToGreyscale
import actors.FileUploader.UploadFile
import models.ImageOperationStatus
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Logger

object ImageSupervisor {
  case class HandleOperation(val operationId: String, val input: String)
  case class ImageReady(val operationId: String, val imagePath: String)
  case class UploadDone(val operationId: String, val url: String)
  case class OperationError(val operationId: String, val component: String, val error: String)
}

class ImageSupervisor(val dao: ImageOperationDAO) extends Actor {
  lazy val imageEditor = ActorSystem("Greyscalr").actorOf(Props[ImageEditor], name = "ImageEditor")
  lazy val imageUploader = ActorSystem("Greyscalr").actorOf(Props[FileUploader], name = "ImageUploader")
  val logger = Logger(getClass.getName)

  def receive = {
    case message: HandleOperation => {
      logger.debug(s"[${message.operationId}}] Converting image ${message.input}")
      imageEditor ! ConvertToGreyscale(message.operationId, message.input)
      updateStatus(message.operationId, ImageOperationStatus.StatusConverting)
    }
    case message: ImageReady => {
      logger.debug(s"[${message.operationId}}] Uploading image")
      imageUploader ! UploadFile(message.operationId, "greyscalr", message.operationId, message.imagePath)
      updateStatus(message.operationId, ImageOperationStatus.StatusUploading)
    }
    case message: UploadDone => {
      logger.debug(s"[${message.operationId}}] Upload done!")
      updateStatus(message.operationId, ImageOperationStatus.StatusUploaded, Some(message.url))
    }
    case message: OperationError => {
      logger.debug(s"[${message.operationId}}] Error: ${message.error}")
      updateStatus(message.operationId, ImageOperationStatus.StatusError)
    }
  }

  def updateStatus(id: String, newStatus: String, newUrl: Option[String] = None) {
    dao.find(id).map {
      _ match {
        case Some(operation) => {
          val updatedOperation = operation.copy(status = newStatus, url = newUrl)
          dao.update(id, updatedOperation)
        }
        case None => ???
      }
    }
  }

}

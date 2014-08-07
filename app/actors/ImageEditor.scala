package actors

import java.util.concurrent.Executors

import actors.FileUploader.UploadFile
import akka.actor.{Props, ActorSystem, Actor}
import javax.imageio.ImageIO
import java.io.File
import models.{ImageOperationStatus, ImageOperation}
import ImageOperation.updateStatus
import akka.pattern.ask
import actors.ImageEditor.ConvertToGreyscale
import play.api.Logger
import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent.{Future, Await, ExecutionContext, Promise}

import scala.util.{Failure, Success}

object ImageEditor {
  val name = "ImageEditor"
  case class ConvertToGreyscale(val operationId: String, val input: String, val output: String)
}

class ImageEditor extends Actor {

  def receive = {
    case operation: ConvertToGreyscale => {
      try {
        updateStatus(operation.operationId, ImageOperationStatus.StatusConverting)
        convertImageToGreyScale(operation.input, operation.output)
        updateStatus(operation.operationId, ImageOperationStatus.StatusImageReady)

        implicit val timeout = Timeout(180 seconds)
        val imageUploader = ActorSystem("Greyscalr").actorOf(Props[FileUploader], name = "ImageEditor")
        val futureUrl = imageUploader ? UploadFile("greyscalr", operation.operationId, operation.output)
        val url = Await.result(futureUrl, timeout.duration).asInstanceOf[String]
        updateStatus(operation.operationId, ImageOperationStatus.StatusUploaded, Some(url.toString))
      }
      catch {
        /*
         * TODO: handle AmazonServiceException and AmazonServiceException
         */
        case e: Throwable => {
          Logger(ImageEditor.name).error(e.getMessage)
          updateStatus(operation.operationId, ImageOperationStatus.StatusError)
        }
      }
    }
  }

  def convertImageToGreyScale(input: String, output: String) = {
    val image = ImageIO.read(new File(input))

    val width = image.getWidth
    val height = image.getHeight

    for (
      i <- 0 to (width - 1);
      j <- 0 to (height - 1)
    ) {
      val pixel = image.getRGB(i, j)

      val red = (pixel >> 16) & 255
      val green = (pixel >> 8) & 255
      val blue = pixel & 255

      val newBlue, newGreen, newRed = (blue + green + red) / 3
      val newRgbValue = (newRed << 16) + (newGreen << 8) + (newBlue)

      image.setRGB(i, j, newRgbValue)
    }
    ImageIO.write(image, "PNG", new File(output))
  }

}

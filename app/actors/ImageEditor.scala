package actors

import akka.actor.Actor
import javax.imageio.ImageIO
import java.io.File
import actors.ImageEditor.ConvertToGreyscale
import models.{ImageOperationStatus, ImageOperation}
import ImageOperation.updateStatus

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
      }
      catch {
        case _: Throwable => updateStatus(operation.operationId, ImageOperationStatus.StatusError)
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

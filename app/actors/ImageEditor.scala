package actors

import akka.actor.Actor
import javax.imageio.ImageIO
import java.io.File
import actors.ImageEditor.ConvertToGreyscale
import models.ImageOperation

object ImageEditor {
  val name = "ImageEditor"

  case class ConvertToGreyscale(val operationId: String, val input: String, val output: String)
}

class ImageEditor extends Actor {

  def receive = {
    case operation: ConvertToGreyscale => {
      try {
        ImageOperation.updateStatus(operation.operationId, ImageOperation.StatusConverting)
        convertImageToGrayScale(operation.input, operation.output)
        ImageOperation.updateStatus(operation.operationId, ImageOperation.StatusImageReady)
      }
      catch {
        case _ => ImageOperation.updateStatus(operation.operationId, ImageOperation.StatusError)
      }
    }
  }

  def convertImageToGrayScale(input: String, output: String) = {
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

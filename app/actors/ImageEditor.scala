package actors


import akka.actor.Actor
import javax.imageio.ImageIO
import java.io.File
import actors.ImageEditor.ConvertToGreyscale

import actors.ImageSupervisor.{OperationError, ImageReady}

object ImageEditor {

  case class ConvertToGreyscale(val operationId: String, val input: String)

}

class ImageEditor extends Actor {

  def receive = {
    case operation: ConvertToGreyscale => {
      try {
        val output = "/tmp/" + operation.operationId.toString + ".png"
        convertImageToGreyScale(operation.input, output)
        sender ! ImageReady(operation.operationId, output)
      }
      catch {
        case e: Throwable => sender ! OperationError(operation.operationId, getClass.getName, e.getMessage)
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

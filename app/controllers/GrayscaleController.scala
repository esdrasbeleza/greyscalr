package controllers

import play.api.mvc._
import java.io.File
import java.util.UUID
import models.ImageOperationStatus
import play.api.libs.json.Json._
import javax.imageio.ImageIO

object GrayscaleController extends Controller {

  def create = {
    Action(parse.multipartFormData) { request =>
      val files = request.body.files
      if (files.isEmpty) {
        BadRequest("You should upload at least one file")
      }
      else {
        val uuid = UUID.randomUUID().toString
        val fullPath = "/tmp/upload-" + uuid + ".original"

        files.head.ref.moveTo(new File(fullPath))
        val status = ImageOperationStatus.create(uuid, fullPath)
        convertImageToGrayScale(fullPath, "/tmp/" + uuid + ".png")
        Created(toJson(status))
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

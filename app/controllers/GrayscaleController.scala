package controllers

import play.api.mvc._
import java.io.File
import models.ImageOperation
import play.api.libs.json.Json._
import javax.imageio.ImageIO
import org.bson.types.ObjectId

object GrayscaleController extends Controller {

  def create = {
    Action(parse.multipartFormData) { request =>
      val files = request.body.files
      if (files.isEmpty) {
        BadRequest("You should upload at least one file")
      }
      else {
        val id = new ObjectId()
        val fullPath = "/tmp/upload-" + id.toString + ".original"

        files.head.ref.moveTo(new File(fullPath))
        val status = ImageOperation.create(id, fullPath)
        convertImageToGrayScale(fullPath, "/tmp/" + id.toString + ".png")
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

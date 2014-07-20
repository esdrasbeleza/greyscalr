package models

import com.mongodb.casbah.Imports._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import db.MongoAccess.{ insert => insertInMongo }
import javax.imageio.ImageIO
import java.io.File
import play.api.Logger

object ImageOperationStatus {
  lazy val logger = Logger("ImageOperation")
  val StatusConverting = "CONVERTING"
  val StatusUploading = "UPLOADING"

  implicit val imageOperationStatusReads: Reads[ImageOperationStatus] = (
      (JsPath \ "id").read[String] and
      (JsPath \ "status").read[String] and
      (JsPath \ "URL").readNullable[String]
    )(apply _)

  implicit val imageOperationStatusWrites: Writes[ImageOperationStatus] = (
      (JsPath \ "id").write[String] and
      (JsPath \ "status").write[String] and
        (JsPath \ "URL").writeNullable[String]
    )(unlift(unapply))

  def create(id: String, localPath: String) = {
    val imageOperation = MongoDBObject("status" -> StatusConverting,
                                       "localPath" -> localPath)

    insertInMongo(imageOperation)
    convertImageToGrayScale(localPath, "/tmp/" + id + ".png")

    new ImageOperationStatus(id, StatusConverting, None)
  }

  def convertImageToGrayScale(input: String, output: String) = {
    logger.debug(s"Reading $input")
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

case class ImageOperationStatus(val id: String, val status: String, val url: Option[String] = None)

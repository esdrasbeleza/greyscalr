package actors

import akka.actor.Actor
import javax.imageio.ImageIO
import java.io.File
import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.services.s3.AmazonS3Client
import models.{ImageOperationStatus, ImageOperation}
import ImageOperation.updateStatus
import actors.ImageEditor.ConvertToGreyscale
import play.api.Logger
import com.amazonaws.services.s3.model.{CannedAccessControlList, PutObjectRequest}

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
        val url = uploadImageToS3(operation.operationId, operation.output)
        updateStatus(operation.operationId, ImageOperationStatus.StatusUploaded, Some(url))
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

  def uploadImageToS3(keyName: String, filePath: String) = {
    val bucketName = "greyscalr"

    val s3Client = new AmazonS3Client(new ProfileCredentialsProvider())
    val file = new File(filePath)
    // TODO: set expiration time
    s3Client.putObject(new PutObjectRequest(bucketName, keyName, file).withCannedAcl(CannedAccessControlList.PublicRead))
    "http://missingurl.com"
  }

}

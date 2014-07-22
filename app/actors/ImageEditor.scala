package actors

import akka.actor.Actor
import javax.imageio.ImageIO
import java.io.File
import actors.ImageEditor.ConvertToGreyscale
import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.services.glacier.model.{AbortMultipartUploadRequest, CompleteMultipartUploadRequest}
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.{UploadPartRequest, InitiateMultipartUploadRequest, PartETag}
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
        uploadImageToS3(operation.output)
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

  def uploadImageToS3(filePath: String) {
    val bucketname = "greyscalr"
    val keyName = "myKey"

    val s3Client = new AmazonS3Client(new ProfileCredentialsProvider())
    val partETags = List[PartETag]()

    val initRequest = new InitiateMultipartUploadRequest(bucketname, keyName)
    val initResponse = s3Client.initiateMultipartUpload(initRequest)

    val file = new File(filePath)
    val contentLength = file.length
    val partSize = 5242880

    try {
        def uploadPart(i: Int, filePosition: Long, currentPartETags: List[PartETag]): List[PartETag] = {
          if (filePosition < contentLength) {
            val currentPart = Math.min(partSize, (contentLength - filePosition))
            val uploadRequest = new UploadPartRequest().withBucketName(bucketname)
              .withUploadId(initResponse.getUploadId)
              .withPartNumber(i)
              .withFileOffset(filePosition)
              .withFile(file)
              .withPartSize(partSize)

            val partETag = s3Client.uploadPart(uploadRequest).getPartETag
            currentPartETags :: uploadPart(i + 1, filePosition + partSize, currentPartETags :+ partETag)
          }
          else {
            currentPartETags
          }
        }

      val finalPartEtags = uploadPart(1, 0, partETags)
      val compRequest = new CompleteMultipartUploadRequest(bucketname, keyName, initResponse.getUploadId, finalPartEtags)
      s3Client.completeMultipartUpload(compRequest)
    }
    catch {
      case e: Throwable => s3Client.abortMultipartUpload(new AbortMultipartUploadRequest(bucketname, keyName, initResponse.getUploadId))
    }
  }

}

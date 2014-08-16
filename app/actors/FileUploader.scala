package actors

import java.io.File

import actors.FileUploader.UploadFile
import akka.actor.Actor
import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.{CannedAccessControlList, PutObjectRequest}
import actors.ImageSupervisor.{UploadDone, OperationError}

object FileUploader {

  case class UploadFile(operationId: String, bucketName: String, keyName: String, filePath: String)

}

class FileUploader extends Actor {

  def receive = {
    case operation: UploadFile => {
      try {
        val s3Client = new AmazonS3Client(new ProfileCredentialsProvider())
        val file = new File(operation.filePath)
        // TODO: set expiration time
        s3Client.putObject(new PutObjectRequest(operation.bucketName, operation.keyName, file).withCannedAcl(CannedAccessControlList.PublicRead))
        val url = s3Client.getUrl(operation.bucketName, operation.keyName).toString
        sender ! UploadDone(operation.operationId, url)
      }
      catch {
        case e: Throwable => sender ! OperationError(operation.operationId, getClass.getName, e.getMessage)
      }
    }
  }

}

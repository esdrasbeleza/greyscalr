package actors

import java.io.File

import actors.FileUploader.UploadFile
import akka.actor.Actor
import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.{CannedAccessControlList, PutObjectRequest}

object FileUploader {
  case class UploadFile(bucketName: String, keyName: String, filePath: String)
}

class FileUploader extends Actor {

  def receive = {
    case operation: UploadFile => {
      val s3Client = new AmazonS3Client(new ProfileCredentialsProvider())
      val file = new File(operation.filePath)
      // TODO: set expiration time
      s3Client.putObject(new PutObjectRequest(operation.bucketName, operation.keyName, file).withCannedAcl(CannedAccessControlList.PublicRead))
      s3Client.getUrl(operation.bucketName, operation.keyName).toString
    }
  }

}

package models

import com.mongodb.casbah.Imports._
import play.api.libs.json._
import db.MongoAccess.{ insert => insertInMongo }
import db.MongoAccess.{ findUsingId => getFromMongWithId }
import db.MongoAccess.{ update => updateInMongo }
import db.MongoAccess.findAll
import play.api.Logger

object ImageOperationStatus {
  val StatusCreated = "CREATED"
  val StatusConverting = "CONVERTING"
  val StatusImageReady = "IMAGE READY"
  val StatusUploading = "UPLOADING"
  val StatusUploaded = "FINISHED"
  val StatusError = "ERROR"
}

object ImageOperation {
  import ImageOperationStatus._

  lazy val logger = Logger("ImageOperation")

  implicit val imageOperationStatusReads = Json.reads[ImageOperation]

  implicit val imageOperationStatusWrites = Json.writes[ImageOperation]

  def list() = {
    findAll().map{ operation =>
      convertMongoEntryToImageOperation(operation)
    }
  }

  def create(id: String) = {
    val imageOperation = MongoDBObject("_id" -> new ObjectId(id),
                                       "status" -> StatusCreated)
    insertInMongo(imageOperation)
    new ImageOperation(id.toString, StatusCreated, None)
  }

  def read(id: String) = {
    val statusInDatabase = getFromMongWithId(id).get
    convertMongoEntryToImageOperation(statusInDatabase)
  }

  def convertMongoEntryToImageOperation(statusInDatabase: DBObject): ImageOperation = {
    val id = statusInDatabase.get("_id").toString
    val status = statusInDatabase.get("status").asInstanceOf[String]
    val url = statusInDatabase.get("url") match {
      case url: String => Some(url)
      case _ => None
    }
    new ImageOperation(id, status, url)
  }

  def updateStatus(id: String, newStatus: String, newUrl: Option[String] = None) = read(id).copy(status = newStatus, url = newUrl).save()

}

case class ImageOperation(val id: String, val status: String, val url: Option[String] = None) {

  def save() {
    val imageOperation = MongoDBObject("status" -> status,
                                       "url" -> url)
    updateInMongo(id, imageOperation)
  }

}

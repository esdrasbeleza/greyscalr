package models

import com.mongodb.casbah.Imports._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import db.MongoAccess.{ insert => insertInMongo }
import db.MongoAccess.{ findUsingId => getFromMongWithId }
import db.MongoAccess.{ update => updateInMongo }
import play.api.Logger

object ImageOperation {
  lazy val logger = Logger("ImageOperation")
  val StatusCreated = "CREATED"
  val StatusConverting = "CONVERTING"
  val StatusImageReady = "IMAGE READY"
  val StatusUploading = "UPLOADING"
  val StatusUploaded = "FINISHED"
  val StatusError = "ERROR"

  implicit val imageOperationStatusReads: Reads[ImageOperation] = (
      (JsPath \ "id").read[String] and
      (JsPath \ "status").read[String] and
      (JsPath \ "URL").readNullable[String]
    )(apply _)

  implicit val imageOperationStatusWrites: Writes[ImageOperation] = (
      (JsPath \ "id").write[String] and
      (JsPath \ "status").write[String] and
        (JsPath \ "URL").writeNullable[String]
    )(unlift(unapply))

  def create(id: String) = {
    val imageOperation = MongoDBObject("_id" -> new ObjectId(id),
                                       "status" -> StatusCreated)
    insertInMongo(imageOperation)
    new ImageOperation(id.toString, StatusConverting, None)
  }

  def read(id: String) = {
    val statusInDatabase = getFromMongWithId(id).get
    val status = statusInDatabase.get("status").asInstanceOf[String]
    val url = statusInDatabase.get("url") match {
      case url: String => Some(url)
      case _ => None
    }
    new ImageOperation(id, status, url)
  }

  def updateStatus(id: String, newStatus: String) = read(id).copy(status = newStatus).save()

}

case class ImageOperation(val id: String, val status: String, val url: Option[String] = None) {

  def save() {
    val imageOperation = MongoDBObject("status" -> status,
                                       "url" -> url)
    updateInMongo(id, imageOperation)
  }

}

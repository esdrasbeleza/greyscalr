package models

import com.mongodb.casbah.Imports._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import db.MongoAccess.{ insert => insertInMongo }
import db.MongoAccess.{ findUsingId => getFromMongWithId }
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

  def create(id: ObjectId, localPath: String) = {
    val imageOperation = MongoDBObject("_id" -> id,
                                       "status" -> StatusConverting,
                                       "localPath" -> localPath)
    insertInMongo(imageOperation)
    new ImageOperationStatus(id.toString, StatusConverting, None)
  }

  def read(id: String) = {
    val statusInDatabase = getFromMongWithId(id).get
    val status = statusInDatabase.get("status").asInstanceOf[String]
    val url = statusInDatabase.get("url") match {
      case url: String => Some(url)
      case _ => None
    }
    new ImageOperationStatus(id, status, url)
  }

}

case class ImageOperationStatus(val id: String, val status: String, val url: Option[String] = None)

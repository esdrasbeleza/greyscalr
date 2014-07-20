package models

import com.mongodb.casbah.Imports._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import db.MongoAccess.{ insert => insertInMongo }
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
    val imageOperation = MongoDBObject("_id" -> id,
                                       "status" -> StatusConverting,
                                       "localPath" -> localPath)
    insertInMongo(imageOperation)
    new ImageOperationStatus(id, StatusConverting, None)
  }

}

case class ImageOperationStatus(val id: String, val status: String, val url: Option[String] = None)

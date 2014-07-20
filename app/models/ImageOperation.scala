package models

import com.mongodb.casbah.Imports._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import db.MongoAccess.{ insert => insertInMongo }
import db.MongoAccess.{ findUsingId => getFromMongWithId }
import play.api.Logger

object ImageOperation {
  lazy val logger = Logger("ImageOperation")
  val StatusConverting = "CONVERTING"
  val StatusUploading = "UPLOADING"

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

  def create(id: ObjectId, localPath: String) = {
    val imageOperation = MongoDBObject("_id" -> id,
                                       "status" -> StatusConverting,
                                       "localPath" -> localPath)
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

}

case class ImageOperation(val id: String, val status: String, val url: Option[String] = None)

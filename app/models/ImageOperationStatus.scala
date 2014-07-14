package models

import com.mongodb.casbah.Imports._
import play.Play
import play.api.libs.json._
import play.api.libs.functional.syntax._

object ImageOperationStatus {
  val StatusConverting = "CONVERTING"
  val StatusUploading = "UPLOADING"

  implicit val imageOperationStatusReads: Reads[ImageOperationStatus] = (
      (JsPath \ "id").read[String] and
      (JsPath \ "status").read[String] and
      (JsPath \ "localPath").read[String] and
      (JsPath \ "URL").readNullable[String]
    )(apply _)

  implicit val imageOperationStatusWrites: Writes[ImageOperationStatus] = (
      (JsPath \ "id").write[String] and
      (JsPath \ "status").write[String] and
        (JsPath \ "localPath").write[String] and
        (JsPath \ "URL").writeNullable[String]
    )(unlift(unapply))

  def create(localPath: String) = {
    val host = Play.application.configuration.getString("mongo.host")
    val port = Play.application.configuration.getString("mongo.port").toInt
    val databaseName = Play.application.configuration.getString("mongo.database")

    val mongoClient = MongoClient(host, port)
    val db = mongoClient(databaseName)
    val collection = db("image_operations")

    val imageOperation = MongoDBObject("status" -> StatusConverting, "localPath" -> localPath)
    collection.insert(imageOperation)

    new ImageOperationStatus(imageOperation._id.get.toString, StatusConverting, localPath, None)
  }
}

case class ImageOperationStatus(val id: String, val status: String,
                                 val localPath: String, val url: Option[String] = None)

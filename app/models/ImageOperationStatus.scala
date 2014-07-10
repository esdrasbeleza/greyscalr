package models

import com.mongodb.casbah.Imports._
import play.Play

object ImageOperationStatus {
  val StatusConverting = "CONVERTING"
  val StatusUploading = "UPLOADING"

  def create(localPath: String) = {
    val host = Play.application.configuration.getString("mongo.host")
    val port = Play.application.configuration.getString("mongo.port").toInt
    val databaseName = Play.application.configuration.getString("mongo.database")

    val mongoClient = MongoClient(host, port)
    val db = mongoClient(databaseName)
    val collection = db("image_operations")

    val imageOperation = MongoDBObject("status" -> StatusConverting, "localPath" -> localPath)
    collection.insert(imageOperation)

    new ImageOperationStatus(imageOperation._id.get.toString, StatusConverting, localPath, "")
  }
}

class ImageOperationStatus(val id: String, val status: String, val localPath: String, val url: String)

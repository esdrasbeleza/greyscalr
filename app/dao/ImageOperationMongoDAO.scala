package dao

import play.Play
import models.ImageOperation
import play.api.libs.json.Json
import reactivemongo.api.MongoDriver
import play.modules.reactivemongo.json.collection.JSONCollection
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import javax.inject.Singleton

@Singleton
class ImageOperationMongoDAO extends ImageOperationDAO {

  lazy val host = Play.application.configuration.getString("mongo.host")
  lazy val port = Play.application.configuration.getString("mongo.port").toInt
  lazy val databaseName = Play.application.configuration.getString("mongo.database")

  lazy val mongoDriver = new MongoDriver
  lazy val connection = mongoDriver.connection(List(host))
  lazy val db = connection(databaseName)
  lazy val collection: JSONCollection = db("image_operations")

  def find(id: String) = collection.find(Json.obj("id" -> id)).cursor[ImageOperation].headOption

  def insert(operation: ImageOperation) = collection.insert(operation)

  def update(id: String, operation: ImageOperation) = {
    val selector = Json.obj("id" -> id)
    collection.update(selector, operation)
  }

  def list(): Future[List[ImageOperation]] = collection.find(Json.obj()).cursor[ImageOperation].collect[List]()
}

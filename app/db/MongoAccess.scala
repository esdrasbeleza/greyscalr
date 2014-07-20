package db

import play.Play
import com.mongodb.casbah.Imports._

object MongoAccess {
  lazy val host = Play.application.configuration.getString("mongo.host")
  lazy val port = Play.application.configuration.getString("mongo.port").toInt
  lazy val databaseName = Play.application.configuration.getString("mongo.database")
  lazy val mongoClient = MongoClient(host, port)
  lazy val db = mongoClient(databaseName)
  lazy val collection = db("image_operations")

  def insert(mongoObject: MongoDBObject) = collection.insert(mongoObject)

  def findUsingId(id: String) = collection.findOneByID(new ObjectId(id))

  def update(id: String, mongoObject: MongoDBObject) = collection.update(MongoDBObject("_id" -> new ObjectId(id)), mongoObject, true)
}

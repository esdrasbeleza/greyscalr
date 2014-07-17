package db

import play.Play
import com.mongodb.casbah.Imports._

object MongoAccess {
  lazy val host = Play.application.configuration.getString("mongo.host")
  lazy val port = Play.application.configuration.getString("mongo.port").toInt
  lazy val databaseName = Play.application.configuration.getString("mongo.database")

  def insert(mongoObject: MongoDBObject) = {
    val mongoClient = MongoClient(host, port)
    val db = mongoClient(databaseName)
    val collection = db("image_operations")
    collection.insert(mongoObject)
  }
}

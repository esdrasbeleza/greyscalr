package controllers

import play.api._
import play.api.mvc._
import play.api.mvc.BodyParsers.parse
import java.io.File
import play.api.libs.json.Reads
import java.util.UUID

object GrayscaleController extends Controller {

  def create = {
    val id = UUID.randomUUID().toString
    val fullPath = "/tmp/upload-" + id
    Action(parse.file(new File(fullPath))) { request =>
      Created(id)
    }
  }

}

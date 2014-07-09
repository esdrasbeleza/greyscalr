package controllers

import play.api._
import play.api.mvc._
import java.io.File
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

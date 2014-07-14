package controllers

import play.api.mvc._
import java.io.File
import java.util.UUID
import models.ImageOperationStatus
import play.api.libs.json.Json._

object GrayscaleController extends Controller {

  def create = {
    val uuid = UUID.randomUUID().toString
    val fullPath = "/tmp/upload-" + uuid
    Action(parse.file(new File(fullPath))) { request =>
      val status = ImageOperationStatus.create(fullPath)
      Created(toJson(status))
    }
  }

}

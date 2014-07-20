package controllers

import play.api.mvc._
import java.io.File
import java.util.UUID
import models.ImageOperationStatus
import play.api.libs.json.Json._

object GrayscaleController extends Controller {

  def create = {
    Action(parse.multipartFormData) { request =>
      val files = request.body.files
      if (files.isEmpty) {
        BadRequest("You should upload at least one file")
      }
      else {
        val uuid = UUID.randomUUID().toString
        val fullPath = "/tmp/upload-" + uuid + ".original"

        files.head.ref.moveTo(new File(fullPath))
        val status = ImageOperationStatus.create(uuid, fullPath)
        Created(toJson(status))
      }
    }
  }

}

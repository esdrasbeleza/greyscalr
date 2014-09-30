package models

import play.api.libs.json._

object ImageOperationStatus {
  val StatusCreated = "CREATED"
  val StatusConverting = "CONVERTING"
  val StatusUploading = "UPLOADING"
  val StatusUploaded = "FINISHED"
  val StatusError = "ERROR"
}

object ImageOperation {

  implicit val imageOperationStatusReads = Json.reads[ImageOperation]

  implicit val imageOperationStatusWrites = Json.writes[ImageOperation]

}

case class ImageOperation(val id: String,
                          val status: String = ImageOperationStatus.StatusCreated,
                          val url: Option[String] = None)
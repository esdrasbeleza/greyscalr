package dao

import models.ImageOperation
import scala.concurrent.Future

trait ImageOperationDAO {

  def list(): Future[List[ImageOperation]]

  def find(id: String): Future[Option[ImageOperation]]

  def insert(operation: ImageOperation)

  def update(id: String, operation: ImageOperation)

}

import dao.{ImageOperationMongoDAO, ImageOperationDAO}
import play.api.GlobalSettings

import com.google.inject.{Guice, AbstractModule}

object Global extends GlobalSettings {

  val injector = Guice.createInjector(new AbstractModule {
    protected def configure(): Unit = {
      bind(classOf[ImageOperationDAO]).to(classOf[ImageOperationMongoDAO])
    }
  })

  override def getControllerInstance[A](controllerClass: Class[A]): A = injector.getInstance(controllerClass)

}

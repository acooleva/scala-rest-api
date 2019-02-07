import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync
import com.google.inject.AbstractModule
import config.DynamoDBClientProvider
import controllers.CarAdvertController
import javax.inject.{Inject, Singleton}
import net.codingwell.scalaguice.ScalaModule
import play.api.{Configuration, Environment}
import repos.{CarAdvertsRepository, CarAdvertsRepositoryImpl}


/**
  * This class is a Guice module that tells Guice how to bind several
  * different types. This Guice module is created when the Play
  * application starts.
  *
  * Play will automatically use any class called `Module` that is in
  * the root package. You can create modules in other locations by
  * adding `play.modules.enabled` settings to the `application.conf`
  * configuration file.
  */
@Singleton
class Module(environment: Environment, configuration: Configuration) extends AbstractModule with ScalaModule {

  override def configure(): Unit = {
    bind[CarAdvertController]

    bind[AmazonDynamoDBAsync].toProvider[DynamoDBClientProvider].in[Singleton]

    bind[CarAdvertsRepository].to[CarAdvertsRepositoryImpl].in[Singleton]
  }
}
package config

import akka.actor.ActorSystem
import awscala.BasicCredentialsProvider
import awscala.dynamodbv2.ProvisionedThroughput
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.services.dynamodbv2.model.{AttributeDefinition, CreateTableRequest, KeySchemaElement}
import com.amazonaws.services.dynamodbv2.{AmazonDynamoDBAsync, AmazonDynamoDBAsyncClientBuilder}
import javax.inject.{Inject, Provider}
import play.api.{Configuration, Logger}

import scala.concurrent.ExecutionContext

/**
  * Provides a fully configured Amazon Asynchronous DynamoDB client and table name to use in a DynamoDB repository.
  * The information used to configure the client is retrieved from the application.conf HOCON file.
  * @param configuration the Typesafe/Lightbend configuration library used to access HOCON files
  */
class DynamoDBClientProvider @Inject() (configuration: Configuration) extends Provider[AmazonDynamoDBAsync] {
  override def get(): AmazonDynamoDBAsync = {
    val endpointConfiguration = new EndpointConfiguration("http://localhost:8000", "us-east-1")
    val optAccessKey = configuration.get[String]("dynamodb.aws-access-key-id")
    val optSecretKey = configuration.get[String]("dynamodb.aws-secret-access-key")

    val dynamoClient: AmazonDynamoDBAsync = AmazonDynamoDBAsyncClientBuilder.standard()
      .withEndpointConfiguration(endpointConfiguration)
      .withCredentials(BasicCredentialsProvider.apply(optAccessKey, optSecretKey))
      .build()

    val createTableRequest = new CreateTableRequest()

    val idAttr = new AttributeDefinition("id", "S")
    val attributes = Seq(idAttr)

    val idHashKeyAttr = new KeySchemaElement("id", "HASH")
    val hashKeyAttributes = Seq(idHashKeyAttr)

    dynamoClient.deleteTable("CAR_ADVERTS")

    val tableRequest = createTableRequest
      .withTableName("CAR_ADVERTS")
      .withAttributeDefinitions(scala.collection.JavaConversions.seqAsJavaList(attributes))
      .withKeySchema(scala.collection.JavaConversions.seqAsJavaList(hashKeyAttributes))
      .withProvisionedThroughput(new ProvisionedThroughput(10, 10))
    dynamoClient.createTable(tableRequest)

    dynamoClient
  }
}

/**
  * Provides a separate execution context meant to be used by a single data store to adhere to the bulkhead pattern.
  * This execution context is retrieved from an Akka dispatcher that uses a fork-join executor thread pool by looking
  * this information up in the application.conf HOCON
  * @param system the Akka actor system from which a dispatcher is looked up and obtained
  */
class RepositoryExecutionContextProvider @Inject()(system: ActorSystem) extends Provider[ExecutionContext] {
    private val log = Logger("Repository ExecutionContext Configuration")
    private val result: ExecutionContext = {
      // look up dispatcher by-name
      val executor = system.dispatchers.lookup("repository-dispatcher")
      log.info(s"Executor: ${executor.id} has been retrieved")
      executor
    }

    override def get(): ExecutionContext = result
}

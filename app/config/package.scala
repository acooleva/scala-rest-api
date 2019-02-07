package config

import play.api.Configuration

package object config {
  object Guice {
    final val DynamoRepository = "Repository"
  }

  object DynamoDB {
    /**
      * DynamoDB settings depend on application.conf
      * To properly configure the DynamoDB client:
      * - for local usage: endpoint + aws keys + table name
      * - for running on AWS with proper role access on the machine: region + table name
      * @param config Play Configuration object used to access application.conf
      */
    case class Settings(config: Configuration) {
      val endpoint = config.get[String]("dynamodb.endpoint")
      val tableName = config.get[String]("dynamodb.table-name");
      val region = config.get[String]("dynamodb.region")
      var accessKey = config.get[String]("dynamodb.aws-access-key-id")
      var secretAccessKey = config.get[String]("dynamodb.aws-secret-access-key")
    }
  }
}
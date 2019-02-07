name := "ScalaRESTApi"
 
version := "1.0" 
      
lazy val `scalarestapi` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  ehcache,
  ws,
  guice,
  "com.gu" %% "scanamo" % "1.0.0-M8",
  "net.codingwell" %% "scala-guice" % "4.0.1",
  "jp.co.bizreach" %% "aws-dynamodb-scala" % "0.0.7",
  "com.gu" %% "scanamo-alpakka" % "1.0.0-M8"
)

// DynamoDB Local
dynamoDBLocalDownloadDir := file("./dynamodb-local")
dynamoDBLocalPort := 8000
dynamoDBLocalSharedDB := true

      
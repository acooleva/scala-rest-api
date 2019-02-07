package repos

import java.util.UUID

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync
import com.gu.scanamo.query.{KeyEquals, UniqueKey}
import com.gu.scanamo.syntax._
import com.gu.scanamo.{ScanamoAsync, Table}
import config.config.DynamoDB
import javax.inject.Inject
import models.CarAdvert
import play.api.Configuration
import config.DynamoDBFormatHelpers._

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

/**
  * Implementation of [[repos.CarAdvertsRepository]].
  *
  * @param config configuration which contains settings for the database.
  * @param client the database client used for executing requests for the database
  * @param executionContext the execution context.
  */
class CarAdvertsRepositoryImpl @Inject()(
    config: Configuration,
    client: AmazonDynamoDBAsync,
    executionContext: ExecutionContext)
  extends CarAdvertsRepository {

  lazy val carAdvertsTableName = {
    val result = DynamoDB.Settings(config).tableName
    result
  }
  val carAdvertsTable = Table[CarAdvert](carAdvertsTableName)

  private def captureAndFail[R](msg: String): PartialFunction[Throwable, Either[RepositoryError, R]] = {
    case t: Throwable =>
      Left[RepositoryError, R](ConnectionError)
  }

  override def create(carAdvert: CarAdvert): Future[Either[RepositoryError, CarAdvert]] = {
    val putRequest = carAdvertsTable.put(carAdvert)
    val result = ScanamoAsync.exec(client)(putRequest)
    result.map(_ => Right(carAdvert)).recover(captureAndFail[CarAdvert]("Create car advert failed"))
  }

  override def readAll: Future[Seq[CarAdvert]] = {
    val scanRequest = carAdvertsTable.scan()
    val result = ScanamoAsync.exec(client)(scanRequest)
    result
      .map(_.toList)
      .map(listXor => {
        val goodResults = listXor.collect {
          case Right(carAdvert) => carAdvert
        }
        goodResults
      })
  }

  override def find(carAdvertId: UUID): Future[Option[CarAdvert]] = {
    val findRequest = carAdvertsTable.get(UniqueKey(KeyEquals('id, carAdvertId)))
    val result = ScanamoAsync.exec(client)(findRequest)
    result.map(r => r.get).map(p => p.fold(
      e => None,
      r => Some(r)
    ))
  }

  override def delete(carAdvertId: UUID): Future[Either[RepositoryError, UUID]] = {
    val deleteRequest = carAdvertsTable.delete(UniqueKey(KeyEquals('id, carAdvertId)))
    val result = ScanamoAsync.exec(client)(deleteRequest)
    result.map(_ => Right(carAdvertId)).recover(captureAndFail("Delete advert failed"))
  }

  override def update(carAdvert: CarAdvert): Future[Either[RepositoryError, CarAdvert]] = {
    val updateRequest = carAdvertsTable.update(
      UniqueKey(KeyEquals('id, carAdvert.id)),
        set('title -> carAdvert.title) and
        set('price -> carAdvert.price) and
        set('fuel -> carAdvert.fuel) and
        set('isNew -> carAdvert.isNew) and
        set('mileAge -> carAdvert.mileAge) and
        set('firstRegistration -> carAdvert.firstRegistration))
    val result = ScanamoAsync.exec(client)(updateRequest)
    result.map(_ => Right(carAdvert)).recover(captureAndFail[CarAdvert]("Update advert failed"))
  }
}

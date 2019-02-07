package repos

import java.util.UUID

import models.CarAdvert

import scala.concurrent.Future


sealed trait RepositoryError
case object ConnectionError extends RepositoryError
case object DeserializationError extends RepositoryError

/**
  * Car adverts repository trait.
  */
trait CarAdvertsRepository {

  /**
    * Creates car advert and stores it into the database.
    *
    * @param carAdvert the advert to be created
    * @return create advert future result.
    */
  def create(carAdvert: CarAdvert): Future[Either[RepositoryError, CarAdvert]]

  /**
    * Reads all car adverts from database.
    *
    * @return read all adverts future result.
    */
  def readAll(): Future[Seq[CarAdvert]]

  /**
    * Finds car advert in database.
    *
    * @param id the id of the advert to be found.
    * @return the find advert future result.
    */
  def find(id: UUID): Future[Option[CarAdvert]]


  /**
    * Deletes car advert from database.
    *
    * @param id the id of the advert to be deleted.
    * @return the delete advert future result.
    */
  def delete(id: UUID): Future[Either[RepositoryError, UUID]]


  /**
    * Updates car advert in database
    *
    * @param carAdvert the advert to be updated.
    * @return the update car advert future result.
    */
  def update(carAdvert: CarAdvert): Future[Either[RepositoryError, CarAdvert]]
}

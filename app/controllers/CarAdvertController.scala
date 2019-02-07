package controllers

import java.util.UUID

import dtos.CarAdvertDto._
import dtos.ErrorResponseDto._
import dtos.{CreateCarAdvertDto, ErrorResponseDto, UpdateCarAdvertDto}
import javax.inject.Inject
import models.{CarAdvert => CarAdvertModel}
import play.api.libs.json._
import play.api.mvc._
import repos.{CarAdvertsRepository, RepositoryError}
import models.CarAdvert

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits._

/**
  * Car advert controller.
  *
  * @param carAdvertsRepository repository for the car adverts.
  */
class CarAdvertController @Inject()(carAdvertsRepository: CarAdvertsRepository) extends InjectedController {

  def create: Action[JsValue] = Action.async(parse.json) {
    implicit request =>
      onValidationHandleSuccess[CreateCarAdvertDto](request.body) {
        createCarAdvertRequest => {
          val carAdvertModel: CarAdvertModel = createCarAdvertRequest.toModel
          handleFutureCreateResult(carAdvertsRepository.create(carAdvertModel)) {
            carAdvert => Created(carAdvert.toDTO.toJson)
          }
        }
      }
  }

  private val ServiceError: Result = ServiceUnavailable(ErrorResponseDto("Service Error", Map.empty).toJson)


  private def validateJsonBody[T](jsonBody: JsValue)(implicit reads: Reads[T]): Either[ErrorResponseDto, T] =
    jsonBody.validate[T].asEither.fold(
      errors => Left(ErrorResponseDto("Validation Error", fmtValidationResults(errors))),
      t => Right(t)
    )

  private def onValidationHandleSuccess[T](jsonBody: JsValue)(successFn: T => Future[Result])
                                          (implicit reads: Reads[T]): Future[Result] =
    validateJsonBody(jsonBody).fold(errorResponse => Future.successful(BadRequest(errorResponse.toJson)), successFn)

  private def handleFutureCreateResult[T](futureCreateResult: Future[Either[RepositoryError, CarAdvertModel]])
                                      (completeFn: CarAdvertModel => Result): Future[Result] =
    futureCreateResult.map {
      case Right(createdCarAdvert) => completeFn(createdCarAdvert)
      case Left(_) => ServiceError
    }

  def readAll: Action[AnyContent] = Action.async {
    carAdvertsRepository.readAll
      .map {
        carAdverts => {
          val jsonCarAdverts = carAdverts.map(advert => advert.toDTO.toJson)
          Ok(JsArray(jsonCarAdverts))
        }
      }
  }

  def delete(carAdvertId: UUID): Action[AnyContent] = Action.async {
    handleFutureDeleteResult(carAdvertsRepository.delete(carAdvertId))
  }

  private def handleFutureDeleteResult[T](futureDeleteResult: Future[Either[RepositoryError, UUID]]): Future[Result] =
    futureDeleteResult.map {
      case Right(uuid) => Ok(Json toJson Map("id" -> uuid.toString))
      case Left(_) => ServiceError
    }

  def read(advertId: UUID): Action[AnyContent] = Action.async {
    handleFutureFindResult(carAdvertsRepository.find(advertId)) {
      carAdvert => Future.successful(Ok(carAdvert.toDTO.toJson))
    }
  }

  private def handleFutureFindResult[T](futureFindResult: Future[Option[CarAdvert]])
                                    (completeFn: CarAdvert => Future[Result]): Future[Result] =
    futureFindResult.flatMap {
      case Some(data) => completeFn(data)
      case None => Future.successful(ServiceError)
    }

  def update(advertId: UUID): Action[JsValue] = Action.async(parse.json) {
    implicit request =>
      onValidationHandleSuccess[UpdateCarAdvertDto](request.body) {
        updateCarAdvertRequest =>
          handleFutureFindResult(carAdvertsRepository.find(advertId)) {
            foundCarAdvert => {
              val updatedCarAdvert = updateCarAdvertRequest.toModel(foundCarAdvert)
              handleFutureUpdateResult(carAdvertsRepository.update(updatedCarAdvert)) {
                updatedCarAdvert => Ok(updatedCarAdvert.toDTO.toJson)
              }
            }
          }
      }
  }

  private def handleFutureUpdateResult[T](futureUpdateResult: Future[Either[RepositoryError, CarAdvertModel]])
                                      (completeFn: CarAdvertModel => Result): Future[Result] =
    futureUpdateResult.map {
      case Right(updatedCarAdvert) => completeFn(updatedCarAdvert)
      case Left(_) => ServiceError
    }
}
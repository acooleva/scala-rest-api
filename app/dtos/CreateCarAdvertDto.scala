package dtos

import java.util.UUID

import enums.Fuel.Fuel
import play.api.libs.json._

import models.{CarAdvert => CarAdvertModel}

case class CreateCarAdvertDto(
  title: String,
  fuel: Fuel,
  price: Int,
  isNew: Boolean,
  mileAge: Int,
  firstRegistration: String)

object CreateCarAdvertDto {

  implicit val jsonWrites = Json.writes[CreateCarAdvertDto]
  implicit val jsonValidatedReads = Reads[CreateCarAdvertDto] {
    json =>
      for {
        title <- (json \ "title").validate[String].filter(title => title.trim.nonEmpty)
        fuel <- (json \ "fuel").validate[Fuel]
        price <- (json \ "price").validate[Int].filter(price => price != null)
        isNew <- (json \ "isNew").validate[Boolean].filter(isNew => isNew != null)
        mileAge <- (json \ "mileAge").validate[Int].filter(price => isNew && price != null)
        firstRegistration <-
          (json \ "firstRegistration").validate[String].filter(firstRegistration => isNew && firstRegistration != null)

      } yield CreateCarAdvertDto(title, fuel, price, isNew, mileAge, firstRegistration);
  }

  implicit class CreateCarAdvertModel(request: CreateCarAdvertDto) {
    def toModel: CarAdvertModel =
      CarAdvertModel(
        UUID.randomUUID(),
        request.title,
        request.fuel.toModel,
        request.price,
        request.isNew,
        request.mileAge,
        request.firstRegistration)
  }
}

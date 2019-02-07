package dtos

import java.util.UUID

import enums.Fuel.Fuel
import models.{CarAdvert => CarAdvertModel}
import play.api.libs.json.{JsValue, Json}
import models.CarAdvert

case class CarAdvertDto(
  id: UUID,
  title: String,
  fuel: Fuel,
  price: Int,
  isNew: Boolean,
  mileAge: Int,
  firstRegistration: String)

object CarAdvertDto {
  implicit val jsonWrites = Json.writes[CarAdvertDto]

  implicit class CarAdvertModelToDto(model: CarAdvertModel) {
    def toDTO: CarAdvertDto =
      CarAdvertDto(model.id, model.title, model.fuel, model.price, model.isNew, model.mileAge, model.firstRegistration)
  }

  implicit class CarAdvertDTOJsonOps(carAdvert: CarAdvertDto) {
    def toJson: JsValue = Json.toJson(carAdvert)
  }
}
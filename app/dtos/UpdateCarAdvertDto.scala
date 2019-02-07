package dtos

import enums.Fuel.Fuel
import models.{CarAdvert => CarAdvertModel}
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json}

case class UpdateCarAdvertDto(title: Option[String], price: Option[Int], isNew: Option[Boolean], fuel: Option[Fuel],
                              mileAge: Option[Int], firstRegistration: Option[String])

object UpdateCarAdvertDto {
  implicit val jsonWrites = Json.writes[UpdateCarAdvertDto]
  implicit val jsonReads = (
      (JsPath \ "title").readNullable[String] and
      (JsPath \ "price").readNullable[Int] and
      (JsPath \ "isNew").readNullable[Boolean] and
      (JsPath \ "fuel").readNullable[Fuel] and
      (JsPath \ "mileAge").readNullable[Int] and
      (JsPath \ "firstRegistration").readNullable[String]
    ) (UpdateCarAdvertDto.apply _)

  implicit class UpdateCarAdvertOps(carAdvert: UpdateCarAdvertDto) {
    def toModel(source: CarAdvertModel): CarAdvertModel =
      source.copy(
        title = carAdvert.title.getOrElse(source.title),
        price = carAdvert.price.getOrElse(source.price),
        isNew = carAdvert.isNew.getOrElse(source.isNew),
        fuel = carAdvert.fuel.map(_.toModel).getOrElse(source.fuel),
        mileAge = carAdvert.mileAge.getOrElse(source.mileAge),
        firstRegistration = carAdvert.firstRegistration.getOrElse(source.firstRegistration)
      )
  }
}
